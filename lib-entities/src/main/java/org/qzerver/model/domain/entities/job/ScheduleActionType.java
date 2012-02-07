package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleActionType implements Coded {

    NOP(0),

    LOCAL_COMMAND(1),

    SSH_COMMAND(2),

    HTTP(3),

    JMX(4),

    SOCKET(5);

    ScheduleActionType(int ordinal) {
        Preconditions.checkState(ordinal == this.ordinal());
    }

    @Override
    public int getCode() {
        return ordinal();
    }
}
