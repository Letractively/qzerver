package org.qzerver.model.domain.action.executor.localcommand.threads;

import org.qzerver.model.domain.action.executor.localcommand.LocalCommandActionResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExecutionThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessExecutionThread.class);

    private static final String THREAD_NAME = "Local process execution";

    private static final int DEFAULT_EXIT_CODE = 0;

    private final Process process;

    private volatile int exitCode;

    private volatile LocalCommandActionResultStatus status;

    public ProcessExecutionThread(Process process) {
        super(THREAD_NAME);
        this.process = process;
        this.exitCode = DEFAULT_EXIT_CODE;
        this.status = LocalCommandActionResultStatus.NORMAL;
    }

    @Override
    public void run() {
        LOGGER.debug("Start waiting for process");

        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            status = LocalCommandActionResultStatus.TERMINATED;
        }

        LOGGER.debug("Finish waiting for process with exit code [{}] and status [{}]", exitCode, status);
    }

    public int getExitCode() {
        return exitCode;
    }

    public LocalCommandActionResultStatus getStatus() {
        return status;
    }

}
