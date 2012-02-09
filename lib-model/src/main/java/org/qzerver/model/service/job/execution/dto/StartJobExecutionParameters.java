package org.qzerver.model.service.job.execution.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class StartJobExecutionParameters implements Serializable {

    private long scheduleJobId;

    @NotNull
    private Date scheduled;

    @NotNull
    private Date fired;

    public Date getFired() {
        return fired;
    }

    public void setFired(Date fired) {
        this.fired = fired;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public long getScheduleJobId() {
        return scheduleJobId;
    }

    public void setScheduleJobId(long scheduleJobId) {
        this.scheduleJobId = scheduleJobId;
    }
}
