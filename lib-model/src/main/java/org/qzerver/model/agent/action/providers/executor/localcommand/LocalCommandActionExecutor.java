package org.qzerver.model.agent.action.providers.executor.localcommand;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.executor.localcommand.threads.ProcessExecutionThread;
import org.qzerver.model.agent.action.providers.executor.localcommand.threads.ProcessOutputThread;
import org.qzerver.model.agent.action.providers.executor.localcommand.threads.ProcessTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalCommandActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCommandActionExecutor.class);

    private static final String CMD_PARAM_NODE = "${nodeAddress}";

    private static final String CMD_PARAM_EXECUTION = "${executionId}";

    private long maxCaptureSize;

    private Validator beanValidator;

    @Override
    public LocalCommandActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        LocalCommandActionDefinition definition = (LocalCommandActionDefinition) actionDefinition;

        LOGGER.debug("Execute local command action on node [{}]", nodeAddress);

        // Process builder
        ProcessBuilder pb = new ProcessBuilder();

        File workingFolder = new File(definition.getDirectory());
        pb.directory(workingFolder);
        pb.redirectErrorStream(definition.isCombineOutput());

        // Environment
        pb.environment().clear();

        if (definition.isEnvironmentInherit()) {
            pb.environment().putAll(System.getenv());
        }

        if (definition.getEnvironmentVariables() != null) {
            for (Map.Entry<String, String> environmentEntry : definition.getEnvironmentVariables().entrySet()) {
                pb.environment().put(environmentEntry.getKey(), environmentEntry.getValue());

            }
        }

        // Parameters list
        List<String> commands = new ArrayList<String>();
        commands.add(definition.getCommand());

        String scheduleExecutionIdText = Long.toString(scheduleExecutionId);

        for (String parameter : definition.getParameters()) {
            if (CMD_PARAM_NODE.equals(parameter)) {
                commands.add(nodeAddress);
            } else if (CMD_PARAM_EXECUTION.equals(parameter)) {
                commands.add(scheduleExecutionIdText);
            } else {
                commands.add(parameter);
            }
        }

        pb.command(commands);

        // Start execution
        Process process;

        try {
            process = pb.start();
        } catch (Exception e) {
            LOGGER.warn("Failed to start a process", e);
            return produceExceptionalResult(e);
        }

        // Wait while process exits and grab all the result
        return executeProcess(process, definition);
    }

    private LocalCommandActionResult produceExceptionalResult(Exception e) {
        LocalCommandActionResult result = new LocalCommandActionResult();

        result.setStatus(LocalCommandActionResultStatus.EXCEPTION);
        result.setExitCode(-1);
        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        result.setSucceed(false);

        LocalCommandActionOutput standardOutput = new LocalCommandActionOutput();
        standardOutput.setStatus(LocalCommandActionOutputStatus.IDLE);
        result.setStdout(standardOutput);

        LocalCommandActionOutput standardError = new LocalCommandActionOutput();
        standardError.setStatus(LocalCommandActionOutputStatus.IDLE);
        result.setStderr(standardError);

        return result;
    }

    private LocalCommandActionResult executeProcess(Process process, LocalCommandActionDefinition definition)
    {
        // Start process thread
        ProcessExecutionThread processExecutionThread = new ProcessExecutionThread(process);
        processExecutionThread.start();

        // Start standard output copier
        ProcessOutputThread processStandardOutputThread = new ProcessOutputThread(
            process.getInputStream(), maxCaptureSize, definition.isSkipStdOutput());
        processStandardOutputThread.start();

        // Start error output copier
        ProcessOutputThread processStandardErrorThread = new ProcessOutputThread(
            process.getErrorStream(), maxCaptureSize, definition.isSkipStdError());
        processStandardErrorThread.start();

        // If timeout is set start watchdog thread. It terminates process thread after timeout exceeds
        if (definition.getTimeoutMs() > 0) {
            ProcessTimeoutThread processTimeoutThread = new ProcessTimeoutThread(
                processExecutionThread, definition.getTimeoutMs());
            processTimeoutThread.start();
        }

        // Wait while process thread exits
        try {
            processExecutionThread.join();
        } catch (InterruptedException e) {
            throw new SystemIntegrityException("Unexpected interruption of joining process execution thread");
        }

        // Release all resources - even the process is not alive (http://kylecartmell.com/?p=9)
        IOUtils.closeQuietly(process.getErrorStream());
        IOUtils.closeQuietly(process.getInputStream());
        IOUtils.closeQuietly(process.getOutputStream());
        process.destroy();

        // Wait while output copiers exit
        processStandardOutputThread.shutdownCapture();
        processStandardErrorThread.shutdownCapture();

        // Compose the result
        LocalCommandActionResult result = new LocalCommandActionResult();
        result.setStatus(processExecutionThread.getStatus());
        result.setExitCode(processExecutionThread.getExitCode());
        result.setStdout(processStandardOutputThread.getActionOutput());
        result.setStderr(processStandardErrorThread.getActionOutput());

        // In case of the process termination change "success" status to "terminated"
        if (result.getStatus() == LocalCommandActionResultStatus.TERMINATED) {
            LocalCommandActionOutput stdout = result.getStdout();
            if (stdout.getStatus() == LocalCommandActionOutputStatus.CAPTURED) {
                stdout.setStatus(LocalCommandActionOutputStatus.TERMINATED);
            }
            LocalCommandActionOutput stderr = result.getStderr();
            if (stderr.getStatus() == LocalCommandActionOutputStatus.CAPTURED) {
                stderr.setStatus(LocalCommandActionOutputStatus.TERMINATED);
            }
        }

        // Succeed status
        boolean succeed = (result.getStatus() == LocalCommandActionResultStatus.EXECUTED) &&
            (result.getExitCode() == definition.getExpectedExitCode());
        result.setSucceed(succeed);

        return result;
    }

    @Required
    public void setMaxCaptureSize(long maxCaptureSize) {
        this.maxCaptureSize = maxCaptureSize;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
