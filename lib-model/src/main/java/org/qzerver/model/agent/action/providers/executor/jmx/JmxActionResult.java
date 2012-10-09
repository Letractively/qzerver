package org.qzerver.model.agent.action.providers.executor.jmx;

import org.qzerver.model.agent.action.providers.ActionResult;

public class JmxActionResult implements ActionResult {

    private String exception;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public boolean isSucceed() {
        return false;
    }
}
