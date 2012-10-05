package org.qzerver.model.domain.action.executor.localcommand;

import org.qzerver.model.domain.action.ActionDefinition;

import java.util.List;
import java.util.Map;

public class LocalCommandActionDefinition implements ActionDefinition {

    private static final String TYPE_IDENTIFIER = "command.local";

    private String command;

    private List<String> parameters;

    private String directory;

    private Map<String, String> environmentVariables;

    private boolean environmentInherit;

    private boolean catchStdOutput;

    private boolean catchStdError;

    private boolean combineOutput;

    private int timeoutMs;

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

    public boolean isCatchStdError() {
        return catchStdError;
    }

    public void setCatchStdError(boolean catchStdError) {
        this.catchStdError = catchStdError;
    }

    public boolean isCatchStdOutput() {
        return catchStdOutput;
    }

    public void setCatchStdOutput(boolean catchStdOutput) {
        this.catchStdOutput = catchStdOutput;
    }

    public boolean isCombineOutput() {
        return combineOutput;
    }

    public void setCombineOutput(boolean combineOutput) {
        this.combineOutput = combineOutput;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String getType() {
        return TYPE_IDENTIFIER;
    }
}
