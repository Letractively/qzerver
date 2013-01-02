package org.qzerver.model.agent.action.providers.executor.sshcommand;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import com.gainmatrix.lib.time.ChronometerTimer;
import com.google.common.base.Preconditions;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class SshCommandActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshCommandActionExecutor.class);

    private static final String CMD_PARAM_NODE = "\\$\\{nodeAddress\\}";

    private static final String CMD_PARAM_EXECUTION = "\\$\\{executionId\\}";

    private static final long CHANNEL_PAUSE_MS = 100;

    private static final int BUFFER_SIZE = 16 * 1024;

    private Validator beanValidator;

    private Chronometer chronometer;

    private long maxCaptureSize;

    @Override
    public SshCommandActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        SshCommandActionDefinition definition = (SshCommandActionDefinition) actionDefinition;

        LOGGER.debug("Execute ssh command action on node [{}]", nodeAddress);

        JSch jsch = new JSch();

        String effectiveCommand = definition.getCommand();
        effectiveCommand = effectiveCommand.replaceAll(CMD_PARAM_NODE, nodeAddress);
        String scheduleExecutionIdText = Long.toString(scheduleExecutionId);
        effectiveCommand = effectiveCommand.replaceAll(CMD_PARAM_EXECUTION, scheduleExecutionIdText);

        try {
            if (definition.getKnownHostPath() != null) {
                jsch.setKnownHosts(definition.getKnownHostPath());
            }


            if (definition.getPrivateKeyPath() != null) {
                jsch.addIdentity(definition.getPrivateKeyPath(), definition.getPrivateKeyPassphrase());
            }

            return processConnection(jsch, definition, nodeAddress, effectiveCommand);
        } catch (Exception e) {
            LOGGER.warn("Fail to execute command", e);
            return produceExceptionalResult(e);
        }
    }

    private SshCommandActionResult produceExceptionalResult(Exception e) {
        SshCommandActionResult result = new SshCommandActionResult();

        result.setExitCode(-1);
        result.setStatus(SshCommandActionResultStatus.EXCEPTION);
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());

        SshCommandActionOutput stdOutOutput = new SshCommandActionOutput();
        stdOutOutput.setStatus(SshCommandActionOutputStatus.IDLE);
        result.setStdout(stdOutOutput);

        SshCommandActionOutput stdErrOutput = new SshCommandActionOutput();
        stdErrOutput.setStatus(SshCommandActionOutputStatus.IDLE);
        result.setStderr(stdErrOutput);

        return result;
    }

    private SshCommandActionResult processConnection(JSch jsch, SshCommandActionDefinition definition,
        String nodeAddress, String effectiveCommand) throws Exception
    {
        Session session = jsch.getSession(definition.getUsername(), nodeAddress, definition.getPort());

        session.setPassword(definition.getPassword());
        session.setTimeout(definition.getReadTimeoutMs());
        session.setDaemonThread(true);

        if (definition.getSshProperties() != null) {
            Properties sshConfiguration = new Properties();
            sshConfiguration.putAll(definition.getSshProperties());
            session.setConfig(sshConfiguration);
        }

        session.connect(definition.getConnectionTimeoutMs());
        try {
            return processSession(session, definition, effectiveCommand);
        } finally {
            session.disconnect();
        }
    }

    private SshCommandActionResult processSession(Session session, SshCommandActionDefinition definition,
        String effectiveCommand) throws Exception
    {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        channel.setXForwarding(false);
        channel.setCommand(effectiveCommand);
        channel.setAgentForwarding(definition.isAgentForwarding());

        if (definition.getEnvironmentVariables() != null) {
            for (Map.Entry<String, String> entry : definition.getEnvironmentVariables().entrySet()) {
                channel.setEnv(entry.getKey(), entry.getValue());
            }
        }

        InputStream strOutStream = null;
        InputStream stdErrStream = null;

        try {
            strOutStream = channel.getInputStream();
            stdErrStream = channel.getErrStream();

            channel.connect();
            try {
                return processChannel(channel, definition, strOutStream, stdErrStream);
            } finally {
                channel.disconnect();
            }
        } finally {
            IOUtils.closeQuietly(strOutStream);
            IOUtils.closeQuietly(stdErrStream);
        }
    }

    private SshCommandActionResult processChannel(ChannelExec channel, SshCommandActionDefinition definition,
        InputStream stdOutStream, InputStream stdErrStream) throws Exception
    {
        ChronometerTimer timer = new ChronometerTimer(chronometer);

        OutputCapturer stdOutCapturer = new OutputCapturer(stdOutStream, definition.isSkipStdOutput());
        OutputCapturer stdErrCapturer = new OutputCapturer(stdErrStream, definition.isSkipStdError());

        while (true) {
            // Read data
            stdOutCapturer.read();
            stdErrCapturer.read();

            // Check is channel already closed
            if (channel.isClosed()) {
                int exitCode = channel.getExitStatus();
                boolean succeed = exitCode == definition.getExpectedExitCode();
                return produceSuccessResult(stdOutCapturer, stdErrCapturer, exitCode, succeed);
            }

            // Check timeout
            if ((definition.getTimeoutMs() > 0) && (timer.elapsed() > definition.getTimeoutMs())) {
                return produceTimeoutResult(stdOutCapturer, stdErrCapturer);
            }

            // Make a pause
            try {
                Thread.sleep(CHANNEL_PAUSE_MS);
            } catch (InterruptedException e) {
                LOGGER.warn("Unexpected interruption");
            }
        }
    }

    private SshCommandActionResult produceSuccessResult(OutputCapturer stdOutCapturer, OutputCapturer stdErrCapturer,
        int exitCode, boolean succeed)
    {
        SshCommandActionResult result = new SshCommandActionResult();

        result.setExitCode(exitCode);
        result.setStatus(SshCommandActionResultStatus.EXECUTED);
        result.setSucceed(succeed);

        result.setStdout(stdOutCapturer.requestOutput());
        result.setStderr(stdErrCapturer.requestOutput());

        return result;
    }

    private SshCommandActionResult produceTimeoutResult(OutputCapturer stdOutCapturer, OutputCapturer stdErrCapturer) {
        SshCommandActionResult result = new SshCommandActionResult();

        result.setExitCode(-1);
        result.setStatus(SshCommandActionResultStatus.TIMEOUT);
        result.setSucceed(false);

        // Change status to "bad" when output is "good" for standard output
        SshCommandActionOutput stdOutOutput = stdOutCapturer.requestOutput();
        if (stdOutOutput.getStatus() == SshCommandActionOutputStatus.CAPTURED) {
            stdOutOutput.setStatus(SshCommandActionOutputStatus.TIMEOUT);
        }
        result.setStdout(stdOutOutput);

        // Change status to "bad" when output is "good" for error output
        SshCommandActionOutput stdErrOutput = stdErrCapturer.requestOutput();
        if (stdErrOutput.getStatus() == SshCommandActionOutputStatus.CAPTURED) {
            stdErrOutput.setStatus(SshCommandActionOutputStatus.TIMEOUT);
        }
        result.setStderr(stdErrOutput);

        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

    @Required
    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    @Required
    public void setMaxCaptureSize(long maxCaptureSize) {
        this.maxCaptureSize = maxCaptureSize;
    }

    private final class OutputCapturer {

        private InputStream inputStream;

        private ByteArrayOutputStream outputBuffer;

        private int readTotal;

        private boolean closed;

        private SshCommandActionOutput output;

        private byte[] buffer;

        private OutputCapturer(InputStream inputStream, boolean skip) {
            this.inputStream = inputStream;
            this.readTotal = 0;
            this.closed = false;
            this.buffer = new byte[BUFFER_SIZE];
            this.outputBuffer = new ByteArrayOutputStream();

            if (skip) {
                this.output = new SshCommandActionOutput(SshCommandActionOutputStatus.SKIPPED, null);
            } else {
                this.output = new SshCommandActionOutput(SshCommandActionOutputStatus.CAPTURED, null);
            }
        }

        public void read() throws IOException {
            while ((!closed) && (inputStream.available() > 0)) {
                // Read from input
                int readOnce = inputStream.read(buffer, 0, BUFFER_SIZE);
                if (readOnce == -1) {
                    closed = true;
                    return;

                }
                // Capture data
                if (output.getStatus() == SshCommandActionOutputStatus.CAPTURED) {
                    readTotal += readOnce;

                    if ((maxCaptureSize > 0) && (readTotal > maxCaptureSize)) {
                        // Write remainder
                        int remainder = readOnce - (int) (readTotal - maxCaptureSize);
                        outputBuffer.write(buffer, 0, remainder);
                        // Writing is finished
                        output.setStatus(SshCommandActionOutputStatus.OVERFLOWED);
                    } else {
                        outputBuffer.write(buffer, 0, readOnce);
                    }
                }
            }
        }

        public SshCommandActionOutput requestOutput() {
            byte[] capturedData = outputBuffer.toByteArray();

            if ((capturedData != null) && (capturedData.length > 0)) {
                output.setData(capturedData);
            }

            return output;
        }
    }

}
