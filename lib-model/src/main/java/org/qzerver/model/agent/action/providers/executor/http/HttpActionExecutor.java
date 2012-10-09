package org.qzerver.model.agent.action.providers.executor.http;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionExecutor.class);

    @Override
    public HttpActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        return null;
    }

}
