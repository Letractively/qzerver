package org.qzerver.model.domain.action.executor.socket;

import org.qzerver.model.domain.action.executor.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketActionExecutor implements ActionExecutor<SocketActionResult, SocketActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketActionExecutor.class);

    @Override
    public SocketActionResult execute(SocketActionDefinition definition, String nodeAddress) {
        return null;
    }
}
