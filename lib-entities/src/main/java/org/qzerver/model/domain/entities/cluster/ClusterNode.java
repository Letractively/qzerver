package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ClusterNode extends AbstractBusinessEntity<Long> {

    public static final int MAX_DOMAIN_LENGTH = 128;

    public static final int MAX_COMMENT_LENGTH = 256;

    private Long id;

    @Min(0)
    private int orderIndex;

    @NotBlank
    @Length(max = MAX_DOMAIN_LENGTH)
    private String domain;

    @Length(max = MAX_COMMENT_LENGTH)
    private String comment;

    @NotNull
    private ClusterGroup group;

    private boolean active;

    public ClusterNode() {
        super(BusinessModel.VERSION);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public ClusterGroup getGroup() {
        return group;
    }

    public void setGroup(ClusterGroup group) {
        this.group = group;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
