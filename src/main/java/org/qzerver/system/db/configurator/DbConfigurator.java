package org.qzerver.system.db.configurator;

import org.springframework.beans.factory.annotation.Required;

public class DbConfigurator {

    private DbConfiguratorType type = DbConfiguratorType.CUSTOM;

    /**
     * Hibernate dialect class
     * @return subclass of org.hibernate.dialect.Dialect
     */
    public String getHibernateDialect() {
        switch (type) {
            case HSQLDB:
                return "org.hibernate.dialect.HSQLDialect";
            case MYSQLINNO:
                return "org.hibernate.dialect.MySQLInnoDBDialect";
            case POSTGRES:
                return "org.hibernate.dialect.PostgreSQLDialect";
            case FIREBIRD:
                return "org.hibernate.dialect.FirebirdDialect";
            case INTERBASE:
                return "org.hibernate.dialect.InterbaseDialect";
            case ORACLE8I:
                return "org.hibernate.dialect.Oracle8iDialect";
            case ORACLE9I:
                return "org.hibernate.dialect.Oracle9iDialect";
            case ORACLE10G:
                return "org.hibernate.dialect.Oracle10gDialect";
            case MSSQL2005:
                return "org.hibernate.dialect.SQLServer2005Dialect";
            case MSSQL2008:
                return "org.hibernate.dialect.SQLServer2008Dialect";
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Quartz adapter class
     * @return subclass of org.quartz.impl.jdbcjobstore.DriverDelegate
     */
    public String getQuartzAdapter() {
        switch (type) {
            case HSQLDB:
                return "org.quartz.impl.jdbcjobstore.HSQLDBDelegate";
            case MYSQLINNO:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case POSTGRES:
                return "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
            case FIREBIRD:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case INTERBASE:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case ORACLE8I:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case ORACLE9I:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case ORACLE10G:
                return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
            case MSSQL2005:
                return "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            case MSSQL2008:
                return "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Database JDBC driver class
     * @return subclass of java.sql.Driver
     */
    public String getJdbcDriver() {
        switch (type) {
            case HSQLDB:
                return "org.hsqldb.jdbc.JDBCDriver";
            case MYSQLINNO:
                return "com.mysql.jdbc.Driver";
            case POSTGRES:
                return "org.postgresql.Driver";
            case FIREBIRD:
                return "org.firebirdsql.jdbc.FBDriver";
            case INTERBASE:
                return "interbase.interclient.Driver";
            case ORACLE8I:
                return "oracle.jdbc.driver.OracleDriver";
            case ORACLE9I:
                return "oracle.jdbc.driver.OracleDriver";
            case ORACLE10G:
                return "oracle.jdbc.driver.OracleDriver";
            case MSSQL2005:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case MSSQL2008  :
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Type of generator
     * @return Is new generator type used
     */
    public boolean isNewGeneratorType() {
        switch (type) {
            case HSQLDB:
                return true;
            case MYSQLINNO:
                return true;
            case POSTGRES:
                return true;
            case FIREBIRD:
                return false;
            case INTERBASE:
                return false;
            case ORACLE8I:
                return true;
            case ORACLE9I:
                return true;
            case ORACLE10G:
                return true;
            case MSSQL2005:
                return true;
            case MSSQL2008  :
                return true;
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    @Required
    public void setType(DbConfiguratorType type) {
        this.type = type;
    }

}
