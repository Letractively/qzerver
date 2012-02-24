package liquibase.spring;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiquibaseInitializer extends SpringLiquibase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseInitializer.class);

    private boolean enabled;

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        LOGGER.info("Liquibase update is {}", enabled ? "active" : "disabled");

        System.setProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY, Boolean.toString(enabled));
        try {
            super.afterPropertiesSet();
        } finally {
            System.clearProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
