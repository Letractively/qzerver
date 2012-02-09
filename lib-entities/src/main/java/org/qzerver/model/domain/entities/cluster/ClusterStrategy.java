package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.beans.Coded;
import com.google.common.base.Preconditions;

public enum ClusterStrategy implements Coded {

    /**
     * Circle strategy moves the rolling node index with each execution
     */
    CIRCLE(0),

    /**
     * Select nodes in random order
     */
    RANDOM(1),

    /**
     * Always start with the first active node
     */
    LINE(2);

    ClusterStrategy(int ordinal) {
        Preconditions.checkState(this.ordinal() == ordinal);
    }

    @Override
    public int getCode() {
        return ordinal();
    }

}
