package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.business.entity.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModelVersionHolder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Cluster group - describes the named and ordered collection of cluster nodes
 */
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

    @Min(0)
    private int nodeCount;

    /**
     * Rolling node index for ClusterStrategy.CIRCLE strategy
     */
    @Min(0)
    private int rollingIndex;

    public ClusterGroup() {
        super(BusinessModelVersionHolder.VERSION);
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

    public void reindexNodes() {
        if (nodes != null) {
            for (int i = 0, size = nodes.size(); i < size; i++) {
                nodes.get(i).setOrderIndex(i);
            }

            nodeCount = nodes.size();
        } else {
            nodeCount = 0;
        }
    }

    public int getRollingIndex() {
        return rollingIndex;
    }

    public void setRollingIndex(int rollingIndex) {
        this.rollingIndex = rollingIndex;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }
}
