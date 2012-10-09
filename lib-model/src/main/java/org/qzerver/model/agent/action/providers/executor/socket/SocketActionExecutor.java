package org.qzerver.model.agent.action.providers.executor.socket;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketActionExecutor.class);

    @Override
    public SocketActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        return null;
    }
}
