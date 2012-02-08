package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ScheduleAction extends AbstractBusinessEntity<Long> {

    private Long id;

    /**
     * Action type
     */
    @NotNull
    private ScheduleActionType type = ScheduleActionType.NOP;

    /**
     * Action configuration (XML or JSON)
     */
    private String definition;

    /**
     * Cluster group to execute. If null then action is executed on localhost
     */
    private ClusterGroup clusterGroup;

    /**
     * Whether the action is referenced by schedule job
     */
    private boolean archived;

    @NotNull
    private Date created;

    public ScheduleAction() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClusterGroup getClusterGroup() {
        return clusterGroup;
    }

    public void setClusterGroup(ClusterGroup clusterGroup) {
        this.clusterGroup = clusterGroup;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public ScheduleActionType getType() {
        return type;
    }

    public void setType(ScheduleActionType type) {
        this.type = type;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
