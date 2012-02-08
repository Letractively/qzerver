package org.qzerver.model.domain.action.socket;

import java.io.Serializable;

public class SocketActionResult implements Serializable {

    private byte[] response;

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }
}
