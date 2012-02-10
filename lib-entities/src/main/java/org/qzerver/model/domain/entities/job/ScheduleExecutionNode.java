package org.qzerver.model.domain.entities.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;
import org.qzerver.model.domain.entities.cluster.ClusterNode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ScheduleExecutionNode extends AbstractBusinessEntity<Long> {

    private Long id;

    /**
     * Order of the node in the execution list
     */
    @Min(0)
    private int orderIndex;

    /**
     * Domain
     */
    @NotBlank
    @Length(max = ClusterNode.MAX_DOMAIN_LENGTH)
    private String domain;

    /**
     * Parent execution
     */
    @NotNull
    private ScheduleExecution execution;

    /**
     * Execution result
     */
    private ScheduleExecutionResult result;

    /**
     * Is node localhost
     */
    private boolean localhost;

    public ScheduleExecutionNode() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ScheduleExecutionResult getResult() {
        return result;
    }

    public void setResult(ScheduleExecutionResult result) {
        this.result = result;
    }

    public boolean isLocalhost() {
        return localhost;
    }

    public void setLocalhost(boolean localhost) {
        this.localhost = localhost;
    }

    public ScheduleExecution getExecution() {
        return execution;
    }

    public void setExecution(ScheduleExecution execution) {
        this.execution = execution;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
