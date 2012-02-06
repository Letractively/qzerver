package org.qzerver.ddl;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;

import java.net.URL;
import java.util.Properties;

/**
 * SQL script generator
 *
 * @see <a href="http://stackoverflow.com/questions/297438/auto-generate-data-schema-from-jpa-annotated-entity-classes">initial sample</a>
 */
public class DdlGenerator {

    public static void main(String[] arguments) throws Exception {
        URL url = DdlGenerator.class.getResource("/configuration/jpa/mappings/entities/abstract.xml");

        Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
        ejb3Configuration.addFile(url.getFile());

   	    Properties dialectProps = new Properties();
    	dialectProps.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

    	Configuration hibernateConfiguration = ejb3Configuration.getHibernateConfiguration();
        Dialect dialect = Dialect.getDialect(dialectProps);

        String[] creationScript = hibernateConfiguration.generateSchemaCreationScript(dialect);
        System.out.println(ArrayUtils.toString(creationScript));
    }
}
