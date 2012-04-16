package org.qzerver.model.domain.action.localcommand;

import com.google.common.base.Preconditions;
import org.qzerver.model.domain.action.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalCommandAction implements ActionExecutor<LocalCommandActionResult, LocalCommandActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCommandAction.class);

    @Override
    public LocalCommandActionResult execute(LocalCommandActionDefinition definition, String nodeAddress) {
        Preconditions.checkNotNull(definition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        LOGGER.debug("Execute local command action on node [{}]", nodeAddress);

        return null;
    }

}
