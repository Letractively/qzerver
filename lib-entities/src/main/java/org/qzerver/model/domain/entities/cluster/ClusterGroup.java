package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ClusterGroup extends AbstractBusinessEntity<Long> {

    public static final int MAX_NAME_LENGTH = 256;

    private Long id;

    /**
     * Group name
     */
    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    /**
     * List of group nodes
     */
    @NotNull
    private List<ClusterNode> nodes;

    /**
     * Node selection strategy
     */
    @NotNull
    private ClusterStrategy strategy = ClusterStrategy.CIRCLE;

    /**
     * Rolling node index for ClusterStrategy.CIRCLE strategy
     */
    @Min(0)
    private int rollingIndex;

    /**
     * Limit the number of all trials. Value 0 means no limit
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

    public ClusterGroup() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClusterNode> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<ClusterNode>();
        }
        return nodes;
    }

    public void setNodes(List<ClusterNode> nodes) {
        this.nodes = nodes;
    }

    public int getRollingIndex() {
        return rollingIndex;
    }

    public void setRollingIndex(int rollingIndex) {
        this.rollingIndex = rollingIndex;
    }

    public ClusterStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ClusterStrategy strategy) {
        this.strategy = strategy;
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
}
