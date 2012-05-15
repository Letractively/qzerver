package org.qzerver.model.domain.action.localcommand;

import com.gainmatrix.lib.file.temporary.TemporaryFileFactory;
import com.google.common.base.Preconditions;
import org.qzerver.model.domain.action.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalCommandActionExecutor
    implements ActionExecutor<LocalCommandActionResult, LocalCommandActionDefinition>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCommandActionExecutor.class);

    private static final String PARAM_NODE = "${node}";

    private static final long MAX_OUTPUT_SIZE = 1024 * 1024;

    private TemporaryFileFactory temporaryFileFactory;

    @Override
    public LocalCommandActionResult execute(LocalCommandActionDefinition definition, String nodeAddress) {
        Preconditions.checkNotNull(definition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        LOGGER.debug("Execute local command action on node [{}]", nodeAddress);

        // Result container
        LocalCommandActionResult result = new LocalCommandActionResult();

        // Process builder
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(definition.getDirectory()));
        pb.redirectErrorStream(definition.isCombineOutput());

        // Environment
        pb.environment().clear();

        if (definition.isEnvironmentInherit()) {
            pb.environment().putAll(System.getenv());
        }

        for (Map.Entry<String, String> environmentEntry : definition.getEnvironmentVariables().entrySet()) {
            pb.environment().put(environmentEntry.getKey(), environmentEntry.getValue());

        }

        // Command list
        List<String> commands = new ArrayList<String>();
        commands.add(definition.getCommand());

        for (String parameter : definition.getParameters()) {
            if (PARAM_NODE.equals(parameter)) {
                commands.add(nodeAddress);
            } else {
                commands.add(parameter);
            }
        }

        pb.command(commands);

        // Start execution
        Process process;

        try {
            process = pb.start();
        } catch (IOException e) {
            LOGGER.warn("Fail to start process", e);
            result.setStatus(LocalCommandActionResultStatus.EXCEPTION);
            return result;
        }

        executeProcess(process, definition, result);

        return result;
    }

    private void executeProcess(Process process, LocalCommandActionDefinition definition,
        LocalCommandActionResult result)
    {
        //
        ProcessExecutionThread processExecutionThread = new ProcessExecutionThread(process);
        processExecutionThread.start();

        ProcessOutputThread processStandardOutputThread = new ProcessOutputThread(
            process.getInputStream(),
            temporaryFileFactory,
            MAX_OUTPUT_SIZE);
        processStandardOutputThread.start();

        ProcessOutputThread processErrorOutputThread = new ProcessOutputThread(
            process.getErrorStream(),
            temporaryFileFactory,
            MAX_OUTPUT_SIZE);
        processErrorOutputThread.start();

        if (definition.getTimeoutMs() > 0) {
            ProcessTimeoutThread processTimeoutThread =
                new ProcessTimeoutThread(processExecutionThread, definition.getTimeoutMs());
            processTimeoutThread.start();
        }

        //
        try {
            processExecutionThread.join();
        } catch (InterruptedException e) {
            LOGGER.warn("Unexpected interruption of joining process execution thread");
        }

        try {
            processStandardOutputThread.join();
        } catch (InterruptedException e) {
            LOGGER.warn("Unexpected interruption of joining process output thread");
        }

        try {
            processErrorOutputThread.join();
        } catch (InterruptedException e) {
            LOGGER.warn("Unexpected interruption of joining process error thread");
        }
    }



}
