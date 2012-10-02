package org.qzerver.model.domain.action.executor.jmx;

import org.qzerver.model.domain.action.executor.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxActionExecutor implements ActionExecutor<JmxActionResult, JmxActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxActionExecutor.class);

    @Override
    public JmxActionResult execute(JmxActionDefinition definition, String nodeAddress) {
        return null;
    }
}
