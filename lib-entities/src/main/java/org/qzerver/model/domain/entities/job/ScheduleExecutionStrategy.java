package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleExecutionStrategy implements Coded {

    /**
     * Circle strategy moves the rolling node index with each execution
     */
    CIRCULAR(0),

    /**
     * Select nodes in random order
     */
    RANDOM(1),

    /**
     * Always start with the first active node
     */
    INDEXED(2);

    ScheduleExecutionStrategy(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

    @Override
    public int getCode() {
        return ordinal();
    }

}
