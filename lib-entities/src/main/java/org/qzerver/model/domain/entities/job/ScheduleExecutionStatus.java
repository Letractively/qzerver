package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleExecutionStatus implements Coded {

    /**
     * Execution succeed
     */
    SUCCEED(0),

    /**
     * All nodes failed
     */
    FAILED(1),

    /**
     * Execution was cancelled
     */
    CANCELED(2),

    /**
     * Unexpected exception was thrown (internal error)
     */
    EXCEPTION(3),

    /**
     * Duration of execution was limited and failed
     */
    TIMEOUT(4);

    ScheduleExecutionStatus(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

    @Override
    public int getCode() {
        return ordinal();
    }
}
