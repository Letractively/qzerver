package org.qzerver.model.agent.action.providers.executor.socket;

import org.qzerver.model.agent.action.providers.ActionResult;

public class SocketActionResult implements ActionResult {

    private byte[] response;

    private boolean succeed;

    private SocketActionResultStatus status;

    private String exceptionClass;

    private String exceptionMessage;

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public SocketActionResultStatus getStatus() {
        return status;
    }

    public void setStatus(SocketActionResultStatus status) {
        this.status = status;
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

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public boolean isSucceed() {
        return succeed;
    }
}
