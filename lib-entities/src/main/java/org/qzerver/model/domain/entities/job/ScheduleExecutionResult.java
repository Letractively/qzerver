package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class ScheduleExecutionResult extends AbstractBusinessEntity<Long> {

    private Long id;

    @NotNull
    private ScheduleExecution execution;

    @NotNull
    private ScheduleExecutionNode node;

    private boolean succeed;

    @NotNull
    private Date started;

    private Date finished;

    private String payload;

    public ScheduleExecutionResult() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleExecution getExecution() {
        return execution;
    }

    public void setExecution(ScheduleExecution execution) {
        this.execution = execution;
    }

    public ScheduleExecutionNode getNode() {
        return node;
    }

    public void setNode(ScheduleExecutionNode node) {
        this.node = node;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date created) {
        this.started = created;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
