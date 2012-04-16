package org.qzerver.system.db.ddl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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

public final class DdlGeneratorApplication {

    private static final String PERSISTENCE_CONFIGURATION = "configuration/entities/jpa/persistence.xml";

    private static final Map<String, DbConfiguratorType> DB_TYPES = ImmutableMap.<String, DbConfiguratorType>builder()
            .put("hsqldb", DbConfiguratorType.HSQLDB)
            .put("mysql", DbConfiguratorType.MYSQL_INNO)
            .put("postgresql", DbConfiguratorType.POSTGRES)
            .put("firebird", DbConfiguratorType.FIREBIRD)
            .put("interbase", DbConfiguratorType.INTERBASE)
            .put("oracle8i", DbConfiguratorType.ORACLE8I)
            .put("oracle9i", DbConfiguratorType.ORACLE9I)
            .put("oracle10g", DbConfiguratorType.ORACLE10G)
            .put("oracle11g", DbConfiguratorType.ORACLE11G)
            .put("mssql2005", DbConfiguratorType.MSSQL2005)
            .put("mssql2008", DbConfiguratorType.MSSQL2008)
            .put("derby", DbConfiguratorType.DERBY_CLIENT)
            .build();

    private DdlGeneratorApplication() {
    }

    public static void main(String[] arguments) throws Exception {
        System.err.println("Arguments: " + ArrayUtils.toString(arguments));
        System.err.println("Classpath: " + System.getProperty("java.class.path"));

        // Resolve output directory
        File targetDir = new File(System.getProperty("user.dir"));

        if ((arguments != null) && (arguments.length > 0)) {
            targetDir = new File(arguments[0]);
            if (!targetDir.isDirectory()) {
                if (!targetDir.mkdirs()) {
                    throw new IOException("Fail to create directory: " + targetDir);
                }
            }
        }

        // Compose mapping for each db type
        for (Map.Entry<String, DbConfiguratorType> dbTypeEntry : DB_TYPES.entrySet()) {
            final String dbName = dbTypeEntry.getKey();
            final DbConfiguratorType dbConfiguratorType = dbTypeEntry.getValue();

            // Hibernate configuration
            Configuration cfg = new Configuration();
            boolean isNewGenerator = DbConfiguratorData.HIBERNATE_NEW_GENERATORS.get(dbConfiguratorType);
            cfg.setProperty("hibernate.id.new_generator_mappings", Boolean.toString(isNewGenerator));

            // Compose configuration
            List<String> mappings = loadMappingList();
            for (String mapping : mappings) {
                cfg.addXML(loadResourceContent(mapping));
            }

            // Get dialect instance
            Properties dialectProps = new Properties();
            dialectProps.put(Environment.DIALECT, DbConfiguratorData.HIBERNATE_DIALECTS.get(dbConfiguratorType));

            Dialect dialect = Dialect.getDialect(dialectProps);

            // Generate create&drop DDL scripts
            final String lineEnding = ";\n";

            String[] scriptCreateDdl = cfg.generateSchemaCreationScript(dialect);
            File scriptCreateFile = new File(targetDir, dbName + "-create.sql");
            FileUtils.writeLines(scriptCreateFile, "UTF-8", Arrays.asList(scriptCreateDdl), lineEnding);

            String[] scriptDropDdl = cfg.generateDropSchemaScript(dialect);
            File scriptDropFile = new File(targetDir, dbName + "-drop.sql");
            FileUtils.writeLines(scriptDropFile, "UTF-8", Arrays.asList(scriptDropDdl), lineEnding);
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

        for (int i = 0, size = nodes.getLength(); i < size; i++) {
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
            content = IOUtils.toString(stream, "UTF-8");
        } finally {
            stream.close();
        }

        return content;
    }

}
