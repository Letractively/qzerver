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

    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    @NotNull
    private List<ClusterNode> nodes;

    @NotNull
    private ClusterStrategy strategy = ClusterStrategy.CIRCLE;

    @Min(0)
    private int index;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ClusterStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ClusterStrategy strategy) {
        this.strategy = strategy;
    }
}
