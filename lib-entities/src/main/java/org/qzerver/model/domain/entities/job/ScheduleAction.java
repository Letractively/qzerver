package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.entity.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.qzerver.model.domain.business.BusinessModelVersionHolder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class ScheduleAction extends AbstractBusinessEntity<Long> {

    public static final int MAX_DEFINITION_LENGTH = 8 * 1024 * 1024;

    public static final int MAX_TYPE_LENGTH = 1024;

    private Long id;

    /**
     * Action type (some kind of tag or class name which describes content in 'definition' property)
     */
    @NotNull
    @Length(max = MAX_TYPE_LENGTH)
    private String type;

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

    /**
     * How many times the action was executed
     */
    @Min(0)
    private int usedCount;

    /**
     * The last time the action was executed
     */
    @NotNull
    private Date usedDate;

    public ScheduleAction() {
        super(BusinessModelVersionHolder.VERSION);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public int incrementUsedCount() {
        usedCount = usedCount + 1;
        return usedCount;
    }

    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Date usedDate) {
        this.usedDate = usedDate;
    }

}
