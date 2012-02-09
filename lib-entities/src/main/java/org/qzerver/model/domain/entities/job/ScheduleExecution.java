package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleExecution extends AbstractBusinessEntity<Long> {

    public static final int MAX_NODE_LENGTH = 128;

    private Long id;

    /**
     * Job reference
     */
    private ScheduleJob job;

    /**
     * Job name (copied from schedule job)
     */
    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    /**
     * Cron expression (copied from schedule job)
     */
    @NotBlank
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    /**
     * Nodes list
     */
    private List<ScheduleExecutionNode> nodes;

    /**
     * Result list
     */
    private List<ScheduleExecutionResult> results;

    /**
     * Moment when execution should be started
     */
    @NotNull
    private Date scheduled;

    /**
     * Moment when execution has been actually started
     */
    @NotNull
    private Date fired;

    /**
     * Moment when execution is finished
     */
    private Date finished;

    /**
     * Whether the execution succeed
     */
    private boolean succeed;

    /**
     * Execution is cancelled
     */
    private boolean cancelled;

    /**
     * Action to execute (copied from schedule job)
     */
    @NotNull
    private ScheduleAction action;

    /**
     * Name of host where the execution happened
     */
    @NotBlank
    @Length(max = MAX_NODE_LENGTH)
    private String hostname;

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

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public void setAction(ScheduleAction action) {
        this.action = action;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

