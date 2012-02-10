package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleExecutionStatus implements Coded {

    /**
     * Execution succeed
     */
    SUCCESS(0),

    /**
     * All nodes failed
     */
    ALL_FAILED(1),

    /**
     * Execution was cancelled
     */
    CANCELED(2),

    /**
     * Unexpected exception was thrown (internal error)
     */
    EXCEPTION(3),

    /**
     * The number of trials was limited and failed
     */
    LIMIT_TRIALS(4),

    /**
     * Duration of execution was limited and failed
     */
    LIMIT_DURATION(5);

    ScheduleExecutionStatus(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

    @Override
    public int getCode() {
        return ordinal();
    }
}
