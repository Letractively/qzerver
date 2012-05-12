package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.entity.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ScheduleAction extends AbstractBusinessEntity<Long> {

    public static final int MAX_DEFINITION_LENGTH = 8 * 1024 * 1024;

    private Long id;

    /**
     * Action type
     */
    @NotNull
    private ScheduleActionType type = ScheduleActionType.LOCAL_COMMAND;

    /**
     * Action configuration (XML or JSON)
     */
    @Size(max = MAX_DEFINITION_LENGTH)
    private byte[] definition;

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

    public byte[] getDefinition() {
        return definition;
    }

    public void setDefinition(byte[] definition) {
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
