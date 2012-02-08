package org.qzerver.model.domain.action.localcommand;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class LocalCommandActionDefinition implements Serializable {

    private String command;

    private List<String> parameters;

    private String directory;

    private Map<String, String> environmentVariables;

    private boolean environmentInherit;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public boolean isEnvironmentInherit() {
        return environmentInherit;
    }

    public void setEnvironmentInherit(boolean environmentInherit) {
        this.environmentInherit = environmentInherit;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
