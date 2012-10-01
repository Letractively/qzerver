package org.qzerver.model.service.job.executor.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class AutomaticJobExecutionParameters implements Serializable {

    /**
     * Scheduled time for the job
     */
    @NotNull
    private Date scheduled;

    /**
     * Actual firing time for the job
     */
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

}
