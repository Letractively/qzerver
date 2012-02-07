package org.qzerver.model.domain.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.action.ScheduleAction;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.NotNull;

public class ScheduleJob extends AbstractBusinessEntity<Long> {

    public static final int MAX_NAME_LENGTH = 128;

    public static final int MAX_DESCRIPTION_LENGTH = 512;

    public static final int MAX_CRON_LENGTH = 64;

    private Long id;

    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    @Length(min = 1, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotBlank
    @Length(max = MAX_CRON_LENGTH)
    private String cron;

    private boolean enabled;

    private boolean standby;

    @NotNull
    private ScheduleAction action;

    @NotNull
    private ScheduleGroup group;

    public ScheduleJob() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public void setAction(ScheduleAction action) {
        this.action = action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public ScheduleGroup getGroup() {
        return group;
    }

    public void setGroup(ScheduleGroup group) {
        this.group = group;
    }
}
