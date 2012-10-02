package org.qzerver.model.domain.action.executor.localcommand;

public enum LocalCommandActionResultStatus {

    /**
     * Command has been executed and exit code is obtained
     */
    NORMAL,

    /**
     * Command was terminated due to timeout or manual termination
     */
    TERMINATED,

    /**
     * Exception on executing the command
     */
    EXCEPTION

}
