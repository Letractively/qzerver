package org.qzerver.system.db.ddl;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.qzerver.system.db.configurator.DbConfigurator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DdlGeneratorApplication {

    private static final String PERSISTENCE_CONFIGURATION = "configuration/entities/jpa/persistence.xml";

    public static void main(String[] arguments) throws Exception {
        System.out.println("Arguments: " + ArrayUtils.toString(arguments));
        System.out.println("Classpath: " + System.getProperty("java.class.path"));

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
        for (DbConfiguratorType dbConfiguratorType : DbConfiguratorType.values()) {
            if (DbConfiguratorType.CUSTOM == dbConfiguratorType) {
                continue;
            }

            // Db configuration
            DbConfigurator dbConfigurator = new DbConfigurator();
            dbConfigurator.setType(dbConfiguratorType);

            // Hibernate configuration
            Configuration cfg = new Configuration();
            cfg.setProperty("hibernate.hbm2ddl.auto", "create");
            cfg.setProperty("hibernate.id.new_generator_mappings", Boolean.toString(dbConfigurator.isNewGeneratorType()));

            // Compose configuration
            List<String> mappings = loadMappingList();
            for (String mapping : mappings) {
                cfg.addXML(loadResourceContent(mapping));
            }

            Properties dialectProps = new Properties();
            dialectProps.put(Environment.DIALECT, dbConfigurator.getHibernateDialect());

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
