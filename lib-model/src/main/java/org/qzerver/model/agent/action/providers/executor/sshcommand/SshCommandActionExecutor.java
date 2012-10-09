package org.qzerver.model.agent.action.providers.executor.sshcommand;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshCommandActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshCommandActionExecutor.class);

    @Override
    public SshCommandActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        return null;
    }

}
