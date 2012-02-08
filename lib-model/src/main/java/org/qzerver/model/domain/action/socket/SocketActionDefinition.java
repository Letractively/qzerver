package org.qzerver.model.domain.action.socket;

import java.io.Serializable;

public class SocketActionDefinition implements Serializable {

    private byte[] message;

    private int port;

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
