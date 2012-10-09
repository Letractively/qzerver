package org.qzerver.model.agent.action.providers.executor.localcommand.threads;

import com.gainmatrix.lib.file.temporary.TemporaryFileFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ProcessOutputThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessOutputThread.class);

    private static final String THREAD_NAME = "Local process output copier";

    private static final int BUFFER_SIZE = 4096;

    private final InputStream inputStream;

    private final TemporaryFileFactory temporaryFileFactory;

    private final long maxSize;

    private volatile byte[] result;

    private volatile boolean succeed;

    private volatile String exception;

    public ProcessOutputThread(InputStream inputStream, TemporaryFileFactory temporaryFileFactory, long maxSize) {
        super(THREAD_NAME);
        this.inputStream = inputStream;
        this.temporaryFileFactory = temporaryFileFactory;
        this.maxSize = maxSize;
    }

    @Override
    public void run() {
        try {
            processOutput();
            succeed = true;
        } catch (IOException e) {
            succeed = false;
            exception = e.getLocalizedMessage();
            LOGGER.error("Fail to capture process output", e);
        }
    }

    private void processOutput() throws IOException {
        File file = temporaryFileFactory.allocateFile();
        try {
            processOutputFile(file);
            result = FileUtils.readFileToByteArray(file);
        } finally {
            temporaryFileFactory.releaseFile(file);
        }
    }

    private void processOutputFile(File outputFile) throws IOException {
        OutputStream outputStream = new FileOutputStream(outputFile);
        try {
            processOutputStream(outputStream);
        } finally {
            outputStream.close();
        }
    }

    private void processOutputStream(OutputStream outputStream) throws IOException {
        // Create buffer
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        long total = 0;

        // Copy data
        while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            total += read;
            if ((maxSize > 0) && (total > maxSize)) {
                throw new IOException("Output exceeds maximum limit");
            }
            outputStream.write(buffer, 0, read);
        }
    }

    public String getException() {
        return exception;
    }

    public byte[] getResult() {
        return result;
    }

    public boolean isSucceed() {
        return succeed;
    }

}
