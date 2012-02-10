package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleActionType implements Coded {

    /**
     * Execute local command
     */
    LOCAL_COMMAND(0, false),

    /**
     * Execute remote command on cluster
     */
    SSH_COMMAND(1, true),

    /**
     * HTTP request on cluster
     */
    HTTP(2, true),

    /**
     * JMX call on cluster
     */
    JMX(3, true),

    /**
     * Socket request on cluster
     */
    SOCKET(4, true);

    private boolean clustered;

    ScheduleActionType(int ordinal, boolean clustered) {
        Preconditions.checkState(ordinal == this.ordinal());
        this.clustered = clustered;
    }

    @Override
    public int getCode() {
        return ordinal();
    }

    public boolean isClustered() {
        return clustered;
    }
}
