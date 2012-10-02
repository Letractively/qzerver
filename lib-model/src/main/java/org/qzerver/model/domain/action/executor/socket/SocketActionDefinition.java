package org.qzerver.model.domain.action.executor.socket;

import org.qzerver.model.domain.action.ActionDefinition;

public class SocketActionDefinition implements ActionDefinition {

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

    @Override
    public void doSomething() {
    }
}
