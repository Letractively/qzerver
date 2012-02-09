package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ScheduleAction extends AbstractBusinessEntity<Long> {

    private Long id;

    /**
     * Action type
     */
    @NotNull
    private ScheduleActionType type = ScheduleActionType.LOCAL_COMMAND;

    /**
     * Action configuration (XML or JSON)
     */
    private String definition;

    /**
     * Whether the action is not referenced by any schedule job
     */
    private boolean archived;

    /**
     * When the action was created
     */
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
