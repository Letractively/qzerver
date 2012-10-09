package org.qzerver.model.agent.action.providers.executor.socket;

import org.qzerver.model.agent.action.providers.ActionResult;

public class SocketActionResult implements ActionResult {

    private byte[] response;

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    @Override
    public boolean isSucceed() {
        return false;
    }
}
