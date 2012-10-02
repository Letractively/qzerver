package org.qzerver.model.domain.action.executor.sshcommand;

import org.qzerver.model.domain.action.ActionResult;

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
