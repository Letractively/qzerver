package org.qzerver.model.domain.action.socket;

import org.qzerver.model.domain.action.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketActionExecutor implements ActionExecutor<SocketActionResult, SocketActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketActionExecutor.class);

    @Override
    public SocketActionResult execute(SocketActionDefinition definition, String nodeAddress) {
        return null;
    }
}
