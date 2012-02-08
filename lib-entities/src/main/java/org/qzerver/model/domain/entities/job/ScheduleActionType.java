package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleActionType implements Coded {

    /**
     * Nothing to do
     */
    NOP(0),

    /**
     * Execute local command
     */
    LOCAL_COMMAND(1),

    /**
     * Execute remote command on cluster
     */
    SSH_COMMAND(2),

    /**
     * HTTP request on cluster
     */
    HTTP(3),

    /**
     * JMX call on cluster
     */
    JMX(4),

    /**
     * Socket request on cluster
     */
    SOCKET(5);

    ScheduleActionType(int ordinal) {
        Preconditions.checkState(ordinal == this.ordinal());
    }

    @Override
    public int getCode() {
        return ordinal();
    }
}
