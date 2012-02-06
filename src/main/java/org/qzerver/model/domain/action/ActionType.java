package org.qzerver.model.domain.action;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ActionType implements Coded {

    EXEC(0),

    HTTP(1),

    JMX(2),

    SOCKET(3),

    SSHEXEC(4);

    ActionType(int ordinal) {
        Preconditions.checkState(ordinal == this.ordinal());
    }

    @Override
    public int getCode() {
        return ordinal();
    }
}
