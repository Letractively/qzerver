package org.qzerver.model.agent.action.providers.executor.sshcommand;

import org.qzerver.model.agent.action.providers.ActionResult;

public class SshCommandActionResult implements ActionResult {

    private int exitCode;

    private String stdout;

    private String stderr;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    @Override
    public boolean isSucceed() {
        return false;
    }
}
