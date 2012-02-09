package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ScheduleActionType implements Coded {

    /**
     * Execute local command
     */
    LOCAL_COMMAND(0, true),

    /**
     * Execute remote command on cluster
     */
    SSH_COMMAND(1, false),

    /**
     * HTTP request on cluster
     */
    HTTP(2, false),

    /**
     * JMX call on cluster
     */
    JMX(3, false),

    /**
     * Socket request on cluster
     */
    SOCKET(4, false);

    private boolean local;

    ScheduleActionType(int ordinal, boolean local) {
        Preconditions.checkState(ordinal == this.ordinal());
        this.local = local;
    }

    @Override
    public int getCode() {
        return ordinal();
    }

    public boolean isLocal() {
        return local;
    }
}
