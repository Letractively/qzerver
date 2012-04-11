package liquibase.ext.slf4j;

import liquibase.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for Liquibase logging subsystem. Translates all messages to SLF4J logger
 */
public class Slf4jLiquibaseLogger implements liquibase.logging.Logger {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLiquibaseLogger.class);

    public int getPriority() {
        return 5;
    }

    public void setName(String name) {
        // nothing
    }

    public void setLogLevel(String logLevel, String logFile) {
        // nothing
    }

    public void severe(String message) {
        LOGGER.error(message);
    }

    public void severe(String message, Throwable e) {
        LOGGER.error(message, e);
    }

    public void warning(String message) {
        LOGGER.warn(message);
    }

    public void warning(String message, Throwable e) {
        LOGGER.warn(message, e);
    }

    public void info(String message) {
        LOGGER.info(message);
    }

    public void info(String message, Throwable e) {
        LOGGER.info(message, e);
    }

    public void debug(String message) {
        LOGGER.debug(message);
    }

    public void debug(String message, Throwable e) {
        LOGGER.debug(message, e);
    }

    public void setLogLevel(String level) {
        // nothing
    }

    public void setLogLevel(LogLevel level) {
        // nothing
    }

    public LogLevel getLogLevel() {
        LogLevel logLevel = LogLevel.OFF;

        if (LOGGER.isErrorEnabled()) {
            logLevel = LogLevel.SEVERE;
        }
        if (LOGGER.isWarnEnabled()) {
            logLevel = LogLevel.WARNING;
        }
        if (LOGGER.isInfoEnabled()) {
            logLevel = LogLevel.INFO;
        }
        if (LOGGER.isDebugEnabled() || LOGGER.isTraceEnabled()) {
            logLevel = LogLevel.DEBUG;
        }

        return logLevel;
    }

}
