package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;
import org.qzerver.model.domain.entities.cluster.ClusterStrategy;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleExecution extends AbstractBusinessEntity<Long> {

    private Long id;

    /**
     * Job definition
     */
    @NotNull
    private ScheduleJob job;

    /**
     * Cron expression copied from schedule job
     */
    @NotBlank
    @Length(max = ScheduleJob.MAX_CRON_LENGTH)
    private String cron;

    /**
     * Nodes list
     */
    @NotNull
    private List<ScheduleExecutionNode> nodes;

    /**
     * Result list
     */
    private List<ScheduleExecutionResult> results;

    /**
     * Current execution node index
     */
    @Min(0)
    private int clusterRollingIndex;

    /**
     * Strategy copied from cluster definition
     */
    @NotNull
    private ClusterStrategy clusterStrategy = ClusterStrategy.CIRCLE;

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
     * Execution is cancelled
     */
    private boolean cancelled;

    /**
     * Action to execute - copied from schedule job
     */
    @NotNull
    private ScheduleAction action;

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

    public int getClusterRollingIndex() {
        return clusterRollingIndex;
    }

    public void setClusterRollingIndex(int clusterRollingIndex) {
        this.clusterRollingIndex = clusterRollingIndex;
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

    public ClusterStrategy getClusterStrategy() {
        return clusterStrategy;
    }

    public void setClusterStrategy(ClusterStrategy clusterStrategy) {
        this.clusterStrategy = clusterStrategy;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public void setAction(ScheduleAction action) {
        this.action = action;
    }
}
