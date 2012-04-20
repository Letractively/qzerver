package org.qzerver.model.domain.action.jmx;

import org.qzerver.model.domain.action.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxActionExecutor implements ActionExecutor<JmxActionResult, JmxActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxActionExecutor.class);

    @Override
    public JmxActionResult execute(JmxActionDefinition definition, String nodeAddress) {
        return null;
    }
}
