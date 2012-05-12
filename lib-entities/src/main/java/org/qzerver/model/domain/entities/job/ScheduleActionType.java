package org.qzerver.model.domain.entities.job;

import com.google.common.base.Preconditions;

public enum ScheduleActionType {

    /**
     * Execute local command
     */
    LOCAL_COMMAND(0),

    /**
     * Execute remote command on cluster
     */
    SSH_COMMAND(1),

    /**
     * HTTP request on cluster
     */
    HTTP(2),

    /**
     * JMX call on cluster
     */
    JMX(3),

    /**
     * Socket request on cluster
     */
    SOCKET(4);

    ScheduleActionType(int ordinal) {
        Preconditions.checkState(ordinal == this.ordinal());
    }

}
