package org.qzerver.model.domain.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleExecution extends AbstractBusinessEntity<Long> {

    private Long id;

    @NotNull
    private ScheduleJob job;

    @NotBlank
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    @NotNull
    private List<ScheduleExecutionNode> nodes;

    private List<ScheduleExecutionResult> results;

    @Min(0)
    private int index;

    @NotNull
    private Date created;

    public ScheduleExecution() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ScheduleJob getJob() {
        return job;
    }

    public void setJob(ScheduleJob job) {
        this.job = job;
    }

    public List<ScheduleExecutionNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<ScheduleExecutionNode>();
        }
        return nodes;
    }

    public void setNodes(List<ScheduleExecutionNode> nodes) {
        this.nodes = nodes;
    }

    public List<ScheduleExecutionResult> getResults() {
        if (results == null) {
            results = new ArrayList<ScheduleExecutionResult>();
        }
        return results;
    }

    public void setResults(List<ScheduleExecutionResult> results) {
        this.results = results;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
