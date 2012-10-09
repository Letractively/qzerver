package org.qzerver.model.agent.action.providers.executor.socket;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

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
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.SOCKET;
    }

}
