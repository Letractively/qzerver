package org.qzerver.model.agent.action.providers.executor.jmx;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxActionExecutor.class);

    @Override
    public JmxActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        return null;
    }
}
