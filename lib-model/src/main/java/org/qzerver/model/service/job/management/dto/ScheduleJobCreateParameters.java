package org.qzerver.model.service.job.management.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleActionType;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStrategy;
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

    @NotNull
    private ScheduleExecutionStrategy strategy = ScheduleExecutionStrategy.CIRCULAR;

    private Long clusterGroupId;

    private long scheduleGroupId;

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

    public long getScheduleGroupId() {
        return scheduleGroupId;
    }

    public void setScheduleGroupId(long scheduleGroupId) {
        this.scheduleGroupId = scheduleGroupId;
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

    public ScheduleExecutionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ScheduleExecutionStrategy strategy) {
        this.strategy = strategy;
    }
}

