package org.qzerver.model.domain.action;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.qzerver.model.domain.business.BusinessModel;
import org.qzerver.model.domain.job.ScheduleJob;

import javax.validation.constraints.NotNull;

public class ScheduleAction extends AbstractBusinessEntity<Long> {

    private Long id;

    @NotNull
    private ScheduleActionType type = ScheduleActionType.EXEC;

    @NotNull
    private ScheduleJob job;

    public ScheduleAction() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleActionType getType() {
        return type;
    }

    public void setType(ScheduleActionType type) {
        this.type = type;
    }

    public ScheduleJob getJob() {
        return job;
    }

    public void setJob(ScheduleJob job) {
        this.job = job;
    }
}
