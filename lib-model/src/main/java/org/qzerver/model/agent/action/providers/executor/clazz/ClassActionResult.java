package org.qzerver.model.agent.action.providers.executor.clazz;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.qzerver.model.agent.action.providers.ActionResult;

public class ClassActionResult implements ActionResult {

    private String result;

    private String exceptionClass;

    private String exceptionMessage;

    private String exceptionStacktrace;

    public ClassActionResult() {
    }

    public ClassActionResult(String result) {
        this.result = result;
    }

    public ClassActionResult(String exceptionClass, String exceptionMessage, String exceptionStacktrace) {
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStacktrace = exceptionStacktrace;
    }

    public ClassActionResult(Exception e) {
        this.exceptionClass = e.getClass().getCanonicalName();
        this.exceptionMessage = e.getLocalizedMessage();
        this.exceptionStacktrace = ExceptionUtils.getStackTrace(e);
    }

    @Override
    public boolean isSucceed() {
        return exceptionClass != null;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public void setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }
}
