package org.qzerver.model.domain.action.sshcommand;

import org.qzerver.model.domain.action.ActionDefinition;

import java.util.List;

public class SshCommandActionDefinition implements ActionDefinition {

    private int port;

    private String username;

    private String password;

    private byte[] privateKey;

    private String command;

    private List<String> parameters;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void doSomethind() {
    }
}
