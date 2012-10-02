package org.qzerver.model.domain.action.executor.jmx;

import org.qzerver.model.domain.action.ActionResult;

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
