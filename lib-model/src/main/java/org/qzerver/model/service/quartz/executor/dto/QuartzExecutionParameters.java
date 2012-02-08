package org.qzerver.model.service.quartz.executor.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class QuartzExecutionParameters implements Serializable {

    private long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
