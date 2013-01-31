package org.qzerver.model.service.job.executor.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

public class AutomaticJobExecutionParameters implements Serializable {

    /**
     * Scheduled time for the job
     */
    @NotNull
    private Date scheduledTime;

    /**
     * Actual firing time for the job
     */
    @NotNull
    private Date firedTime;

    /**
     * Next firing time if it exists
     */
    private Date nextFireTime;

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Date getFiredTime() {
        return firedTime;
    }

    public void setFiredTime(Date firedTime) {
        this.firedTime = firedTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }
}
