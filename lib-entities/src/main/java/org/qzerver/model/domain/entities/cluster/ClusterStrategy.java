package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ClusterStrategy implements Coded {

    CIRCLE(0),

    RANDOM(1),

    LINE(2);

    ClusterStrategy(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

    @Override
    public int getCode() {
        return ordinal();
    }

}
