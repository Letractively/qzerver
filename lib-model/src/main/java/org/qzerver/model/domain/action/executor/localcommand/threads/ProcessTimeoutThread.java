package org.qzerver.model.domain.action.executor.localcommand.threads;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTimeoutThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTimeoutThread.class);

    private static final String THREAD_NAME = "Process timeout watchdog";

    private final Thread watched;

    private final int timeoutMs;

    public ProcessTimeoutThread(Thread watched, int timeoutMs) {
        super(THREAD_NAME);
        Preconditions.checkNotNull(watched, "Watched thread is not set");
        Preconditions.checkArgument(timeoutMs > 0, "Timeout must be positive");
        this.watched = watched;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public void run() {
        try {
            watched.join(timeoutMs);
        } catch (InterruptedException e) {
            LOGGER.warn("Watchdog thread is unexpectedly interrupted");
        }

        if (watched.isAlive()) {
            watched.interrupt();
        }
    }

}
