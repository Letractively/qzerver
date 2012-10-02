package org.qzerver.model.domain.action.executor.sshcommand;

import org.qzerver.model.domain.action.executor.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshCommandActionExecutor implements ActionExecutor<SshCommandActionResult, SshCommandActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshCommandActionExecutor.class);

    @Override
    public SshCommandActionResult execute(SshCommandActionDefinition definition, String nodeAddress) {
        return null;
    }

}
