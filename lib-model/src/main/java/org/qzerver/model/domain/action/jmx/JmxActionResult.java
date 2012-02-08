package org.qzerver.model.domain.action.jmx;

import java.io.Serializable;

public class JmxActionResult implements Serializable {

    private String exception;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
