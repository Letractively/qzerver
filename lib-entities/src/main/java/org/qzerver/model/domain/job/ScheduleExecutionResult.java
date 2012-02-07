package org.qzerver.model.domain.job;

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
    private Date created;

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
