package org.qzerver.model.agent.action.providers.executor.localcommand.threads;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionOutput;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionOutputStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ProcessOutputThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessOutputThread.class);

    private static final String THREAD_NAME = "Local process output copier";

    private static final int BUFFER_SIZE = 16 * 1024;

    private static final int WAIT_AFTER_PROCESS_MS = 1000;

    private final InputStream inputStream;

    private final long maxCaptureSize;

    private final boolean skip;

    private ByteArrayOutputStream outputStream;

    private LocalCommandActionOutputStatus status;

    private byte[] data;

    private String excepionClass;

    private String exceptionMessage;

    public ProcessOutputThread(InputStream inputStream, long maxCaptureSize, boolean skip) {
        this.inputStream = inputStream;
        this.maxCaptureSize = maxCaptureSize;
        this.skip = skip;
        this.status = LocalCommandActionOutputStatus.IDLE;
        this.outputStream = new ByteArrayOutputStream();

        setDaemon(false);
        setName(THREAD_NAME);
    }

    @Override
    public void run() {
        LOGGER.debug("Capture thread is starting");

        if (skip) {
            skipProcessOutput();
        } else {
            captureProcessOutput();
        }

        LOGGER.debug("Capture thread is finishing");
    }

    private void captureProcessOutput() {
        status = LocalCommandActionOutputStatus.CAPTURED;

        try {
            captureStreamOutput(inputStream, outputStream);
        } catch (Exception e) {
            LOGGER.error("Fail to capture process output", e);
            status = LocalCommandActionOutputStatus.EXCEPTION;
            excepionClass = e.getClass().getCanonicalName();
            exceptionMessage = e.getLocalizedMessage();
        }
    }

    private void captureStreamOutput(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int readOnce;
        long readTotal = 0;

        while ((readOnce = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            readTotal += readOnce;

            if ((maxCaptureSize > 0) && (readTotal > maxCaptureSize)) {
                status = LocalCommandActionOutputStatus.OVERFLOWED;
                // Write remainder
                int remainder = readOnce - (int) (readTotal - maxCaptureSize);
                if (remainder > 0) {
                    outputStream.write(buffer, 0, remainder);
                }
                // Throw out of all the rest
                skipStreamOutput(inputStream);
                return;
            }

            outputStream.write(buffer, 0, readOnce);
        }
    }

    private void skipProcessOutput() {
        status = LocalCommandActionOutputStatus.SKIPPED;

        try {
            skipStreamOutput(inputStream);
        } catch (Exception e) {
            LOGGER.error("Fail to skip process output", e);
        }
    }

    private void skipStreamOutput(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
            // do nothing - just skip the output
        }
    }

    public void shutdownCapture() {
        try {
            this.join(WAIT_AFTER_PROCESS_MS);
        } catch (InterruptedException e) {
            throw new SystemIntegrityException("Unexpected output thread join interruption", e);
        }

        // For some unknown reason commands like "bash -c 'sleep 600'" can be terminated on timeout but read
        // operations on the process streams are in block forever. Thread interruption, thread stopping nor stream
        // closing doesn't help at all. All we can do is just to print the warning - capturing thread remains blocked.
        if (this.isAlive()) {
            LOGGER.warn("Capture thread hung after process has stopped");
            this.status = LocalCommandActionOutputStatus.HUNG;
        }

        // Store the data
        if (status.isCaptured()) {
            byte[] capturedData = outputStream.toByteArray();
            if ((capturedData != null) && (capturedData.length > 0)) {
                data = capturedData;
            }
        }
    }

    public LocalCommandActionOutput getActionOutput() {
        LocalCommandActionOutput output = new LocalCommandActionOutput();
        output.setStatus(status);
        output.setData(data);
        output.setExceptionClass(excepionClass);
        output.setExceptionMessage(exceptionMessage);
        return output;
    }

}
