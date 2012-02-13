package org.qzerver.model.service.job.management.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleActionType;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.system.validation.Cron;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ScheduleJobCreateParameters implements Serializable {

    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    @Length(max = ScheduleJob.MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotBlank
    @Cron
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    @NotBlank
    @Length(max = ScheduleJob.MAX_TIMEZONE_LENGTH)
    private String timezone;

    @NotNull
    private ScheduleActionType actionType;

    private Long clusterGroupId;

    private long schedulerGroupId;

    private boolean concurrent;

    private boolean enabled;

    public ScheduleActionType getActionType() {
        return actionType;
    }

    public void setActionType(ScheduleActionType actionType) {
        this.actionType = actionType;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
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

    public Long getClusterGroupId() {
        return clusterGroupId;
    }

    public void setClusterGroupId(Long clusterGroupId) {
        this.clusterGroupId = clusterGroupId;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public long getSchedulerGroupId() {
        return schedulerGroupId;
    }

    public void setSchedulerGroupId(long schedulerGroupId) {
        this.schedulerGroupId = schedulerGroupId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}

