package org.qzerver.system.db.ddl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.qzerver.system.db.configurator.DbConfiguratorData;
import org.qzerver.system.db.configurator.DbConfiguratorType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

public class DdlGeneratorApplication {

    private static final String PERSISTENCE_CONFIGURATION = "configuration/entities/jpa/persistence.xml";

    public static void main(String[] arguments) throws Exception {
        System.err.println("Arguments: " + ArrayUtils.toString(arguments));
        System.err.println("Classpath: " + System.getProperty("java.class.path"));

        // Resolve output directory
        File targetDir = new File(System.getProperty("user.dir"));

        if ((arguments != null) && (arguments.length > 0)) {
            targetDir = new File(arguments[0]);
            if (! targetDir.isDirectory()) {
                if (! targetDir.mkdirs()) {
                    throw new IOException("Fail to create directory: " + targetDir);
                }
            }
        }

        // Compose mapping for each db type
        Set<DbConfiguratorType> dbConfiguratorTypes = ImmutableSet.of(
                DbConfiguratorType.HSQLDB,
                DbConfiguratorType.MYSQL_INNO,
                DbConfiguratorType.POSTGRES,
                DbConfiguratorType.FIREBIRD,
                DbConfiguratorType.INTERBASE,
                DbConfiguratorType.ORACLE8I,
                DbConfiguratorType.ORACLE9I,
                DbConfiguratorType.ORACLE10G,
                DbConfiguratorType.ORACLE11G,
                DbConfiguratorType.MSSQL2005,
                DbConfiguratorType.MSSQL2008,
                DbConfiguratorType.DERBY_CLIENT
        );

        for (DbConfiguratorType dbConfiguratorType : dbConfiguratorTypes) {
            // Hibernate configuration
            Configuration cfg = new Configuration();
            cfg.setProperty("hibernate.id.new_generator_mappings", Boolean.toString(DbConfiguratorData.HIBERNATE_NEW_GENERATORS.get(dbConfiguratorType)));

            // Compose configuration
            List<String> mappings = loadMappingList();
            for (String mapping : mappings) {
                cfg.addXML(loadResourceContent(mapping));
            }

            Properties dialectProps = new Properties();
            dialectProps.put(Environment.DIALECT, DbConfiguratorData.HIBERNATE_DIALECTS.get(dbConfiguratorType));

            Dialect dialect = Dialect.getDialect(dialectProps);

            String[] script;
            String lineEnding = ";\n";

            script = cfg.generateSchemaCreationScript(dialect);
            FileUtils.writeLines(new File(targetDir, dbConfiguratorType + "-create.sql"), "UTF-8", Arrays.asList(script), lineEnding);

            script = cfg.generateDropSchemaScript(dialect);
            FileUtils.writeLines(new File(targetDir, dbConfiguratorType + "-drop.sql"), "UTF-8", Arrays.asList(script), lineEnding);
        }
    }

    private static List<String> loadMappingList() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        String persistenceContent = loadResourceContent(PERSISTENCE_CONFIGURATION);
        InputSource persistenceSource = new InputSource(new StringReader(persistenceContent));
        Document document = documentBuilder.parse(persistenceSource);

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("/persistence/persistence-unit/mapping-file");

        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        List<String> result = new ArrayList<String>(nodes.getLength());

        for (int i=0, size=nodes.getLength(); i < size; i++) {
            Node node = nodes.item(i);
            result.add(node.getTextContent());
        }

        return result;
    }

    private static String loadResourceContent(String resource) throws Exception {
        InputStream stream = DdlGeneratorApplication.class.getClassLoader().getResourceAsStream(resource);
        Preconditions.checkNotNull(stream, "Resource is not found : " + resource);

        String content;

        try {
            content = IOUtils.toString(stream);
        } finally {
            stream.close();
        }

        return content;
    }

}
