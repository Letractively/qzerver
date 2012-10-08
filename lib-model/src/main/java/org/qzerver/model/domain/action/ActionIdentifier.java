package org.qzerver.model.domain.action;

public enum ActionIdentifier {

    LOCAL_COMMAND("command.local"),

    SSH_COMMAND("command.ssh"),

    HTTP("http"),

    SOCKET("socket"),

    JMX("jmx");

    private String typeIdentifier;

    private ActionIdentifier(String typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    public String getTypeIdentifier() {
        return typeIdentifier;
    }

}
