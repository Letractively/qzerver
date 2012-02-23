package org.qzerver.system.db.configurator;

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
            case MYSQL_INNO:
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
            case ORACLE11G:
                return "org.hibernate.dialect.Oracle10gDialect";
            case MSSQL2005:
                return "org.hibernate.dialect.SQLServer2005Dialect";
            case MSSQL2008:
                return "org.hibernate.dialect.SQLServer2008Dialect";
            case DERBY_EMBEDDED:
                return "org.hibernate.dialect.DerbyTenSevenDialect";
            case DERBY_CLIENT:
                return "org.hibernate.dialect.DerbyTenSevenDialect";
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
            case MYSQL_INNO:
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
                return "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
            case ORACLE11G:
                return "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
            case MSSQL2005:
                return "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            case MSSQL2008:
                return "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
            case DERBY_EMBEDDED:
                return "org.quartz.impl.jdbcjobstore.CloudscapeDelegate";
            case DERBY_CLIENT:
                return "org.quartz.impl.jdbcjobstore.CloudscapeDelegate";
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
            case MYSQL_INNO:
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
                return "oracle.jdbc.OracleDriver";
            case ORACLE10G:
                return "oracle.jdbc.OracleDriver";
            case ORACLE11G:
                return "oracle.jdbc.OracleDriver";
            case MSSQL2005:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case MSSQL2008  :
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case DERBY_EMBEDDED:
                return "org.apache.derby.jdbc.EmbeddedDriver";
            case DERBY_CLIENT:
                return "org.apache.derby.jdbc.ClientDriver";
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    /**
     * Get test SQL query
     * @return SQL query text
     */
    public String getTestSQLQuery() {
        switch (type) {
            case HSQLDB:
                return "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
            case MYSQL_INNO:
                return "SELECT 1";
            case POSTGRES:
                return "SELECT 1";
            case FIREBIRD:
                return "SELECT 1";
            case INTERBASE:
                return "SELECT 1";
            case ORACLE8I:
                return "SELECT 1 FROM DUAL";
            case ORACLE9I:
                return "SELECT 1 FROM DUAL";
            case ORACLE10G:
                return "SELECT 1 FROM DUAL";
            case ORACLE11G:
                return "SELECT 1 FROM DUAL";
            case MSSQL2005:
                return "SELECT 1";
            case MSSQL2008  :
                return "SELECT 1";
            case DERBY_EMBEDDED:
                return "SELECT 1 FROM SYSIBM.SYSDUMMY1";
            case DERBY_CLIENT:
                return "SELECT 1 FROM SYSIBM.SYSDUMMY1";
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
            case MYSQL_INNO:
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
            case ORACLE11G:
                return true;
            case MSSQL2005:
                return true;
            case MSSQL2008:
                return true;
            case DERBY_EMBEDDED:
                return true;
            case DERBY_CLIENT:
                return true;
            default:
                throw new IllegalStateException("DB type is not supported: " + type);
        }
    }

    public void setType(DbConfiguratorType type) {
        this.type = type;

        switch (type) {
            case DERBY_CLIENT:
            case DERBY_EMBEDDED:
                System.setProperty("derby.stream.error.file", "error.txt");
        }
    }

}
