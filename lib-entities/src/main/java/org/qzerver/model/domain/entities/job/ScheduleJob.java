package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ScheduleJob extends AbstractBusinessEntity<Long> {

    public static final int MAX_NAME_LENGTH = 128;

    public static final int MAX_DESCRIPTION_LENGTH = 512;

    public static final int MAX_CRON_LENGTH = 64;

    private Long id;

    /**
     * Visible job name
     */
    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    /**
     * Short job description
     */
    @Length(min = 1, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    /**
     * Cron expression
     */
    @NotBlank
    @Length(max = MAX_CRON_LENGTH)
    private String cron;

    /**
     * Is job enabled
     */
    private boolean enabled;

    /**
     * Is job current on pause
     */
    private boolean standby;

    /**
     * Group of the job
     */
    @NotNull
    private ScheduleGroup group;

    /**
     * Action to execute when fired
     */
    @NotNull
    private ScheduleAction action;

    /**
     * Are several instances of this job allowed to execute
     */
    private boolean concurrent;

    /**
     * Cluster group to execute. If null then action is executed on localhost
     */
    private ClusterGroup cluster;

    /**
     * How to choose nodes from cluster
     */
    @NotNull
    private ScheduleExecutionStrategy strategy = ScheduleExecutionStrategy.CIRCULAR;

    /**
     * Limit the number of node trials. Value 0 means no limit
     */
    @Min(0)
    private int trials;

    /**
     * Limit the duration of all trials in milliseconds. Value 0 means no limit
     */
    @Min(0)
    private int timeout;

    /**
     * Execute action on all nodes
     */
    private boolean allNodes;

    public ScheduleJob() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public ScheduleGroup getGroup() {
        return group;
    }

    public void setGroup(ScheduleGroup group) {
        this.group = group;
    }

    public ScheduleAction getAction() {
        return action;
    }

    public void setAction(ScheduleAction action) {
        this.action = action;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public ClusterGroup getCluster() {
        return cluster;
    }

    public void setCluster(ClusterGroup cluster) {
        this.cluster = cluster;
    }

    public int getTrials() {
        return trials;
    }

    public void setTrials(int trials) {
        this.trials = trials;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAllNodes() {
        return allNodes;
    }

    public void setAllNodes(boolean allNodes) {
        this.allNodes = allNodes;
    }

    public ScheduleExecutionStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ScheduleExecutionStrategy strategy) {
        this.strategy = strategy;
    }
}
