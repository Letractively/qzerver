package liquibase.spring;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

public class LiquibaseInitializer extends SpringLiquibase {

    private boolean enabled;

    @Override
    public void afterPropertiesSet() throws LiquibaseException {
        System.setProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY, Boolean.toString(enabled));
        super.afterPropertiesSet();
        System.getProperties().remove(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
