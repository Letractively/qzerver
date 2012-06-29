package org.qzerver.system.db.ddl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.*;
import java.util.*;

public final class DdlGeneratorApplication {

    private static final String PERSISTENCE_CONFIGURATION =
        "org/qzerver/resources/configuration/entities/jpa/persistence.xml";

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
        // Resolve output directory
        String targetDirName = System.getProperty("user.dir");
        File targetDir = new File(targetDirName);

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
            String isNewGeneratorAsString = Boolean.toString(isNewGenerator);
            cfg.setProperty("hibernate.id.new_generator_mappings", isNewGeneratorAsString);

            // Compose configuration
            List<String> mappings = loadMappingList();
            for (String mapping : mappings) {
                String resourceContent = loadResourceContent(mapping);
                cfg.addXML(resourceContent);
            }

            // Get dialect instance
            Properties dialectProps = new Properties();
            String dialectClassName = DbConfiguratorData.HIBERNATE_DIALECTS.get(dbConfiguratorType);
            dialectProps.put(Environment.DIALECT, dialectClassName);

            Dialect dialect = Dialect.getDialect(dialectProps);

            // Generate create&drop DDL scripts
            final String lineEnding = ";\n";

            String[] scriptCreateDdlArray = cfg.generateSchemaCreationScript(dialect);
            List<String> scriptCreateDdlList = Arrays.asList(scriptCreateDdlArray);
            File scriptCreateFile = new File(targetDir, dbName + "-create.sql");
            FileUtils.writeLines(scriptCreateFile, "UTF-8", scriptCreateDdlList, lineEnding);

            String[] scriptDropDdlArray = cfg.generateDropSchemaScript(dialect);
            List<String> scriptDropDdlList = Arrays.asList(scriptDropDdlArray);
            File scriptDropFile = new File(targetDir, dbName + "-drop.sql");
            FileUtils.writeLines(scriptDropFile, "UTF-8", scriptDropDdlList, lineEnding);
        }
    }

    private static List<String> loadMappingList() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        String persistenceContent = loadResourceContent(PERSISTENCE_CONFIGURATION);
        Reader persistenceReader = new StringReader(persistenceContent);
        InputSource persistenceSource = new InputSource(persistenceReader);
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
