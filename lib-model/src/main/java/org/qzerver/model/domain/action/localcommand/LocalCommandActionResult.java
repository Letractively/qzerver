package org.qzerver.model.domain.action.localcommand;

import org.qzerver.model.domain.action.ActionResult;

/**
 * Result from local command action
 */
public class LocalCommandActionResult implements ActionResult {

    private static final int DEFAULT_EXPECTED_EXIT_CODE = 0;

    /**
     * Actual exit code we get from a program
     */
    private int actualExitCode;

    /**
     * Expected exit code. This code means success
     */
    private int expectedExitCode = DEFAULT_EXPECTED_EXIT_CODE;

    /**
     * What we got from standard output (or both from standard output and error output). May be null if output is
     * not required
     */
    private byte[] stdout;

    /**
     * What we got from error output. May be null if output is not required.
     */
    private byte[] stderr;

    public int getExpectedExitCode() {
        return expectedExitCode;
    }

    public void setExpectedExitCode(int expectedExitCode) {
        this.expectedExitCode = expectedExitCode;
    }

    public int getActualExitCode() {
        return actualExitCode;
    }

    public void setActualExitCode(int actualExitCode) {
        this.actualExitCode = actualExitCode;
    }

    public byte[] getStderr() {
        return stderr;
    }

    public void setStderr(byte[] stderr) {
        this.stderr = stderr;
    }

    public byte[] getStdout() {
        return stdout;
    }

    public void setStdout(byte[] stdout) {
        this.stdout = stdout;
    }

    @Override
    public boolean isSucceed() {
        return actualExitCode == expectedExitCode;
    }

}
