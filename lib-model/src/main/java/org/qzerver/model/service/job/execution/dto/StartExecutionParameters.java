package org.qzerver.model.service.job.execution.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class StartExecutionParameters implements Serializable {

    @NotNull
    private Date scheduled;

    @NotNull
    private Date fired;

    private boolean manual;

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

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
