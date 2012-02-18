package org.qzerver.model.domain.entities.cluster;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ClusterNode extends AbstractBusinessEntity<Long> {

    public static final int MAX_ADDRESS_LENGTH = 128;

    public static final int MAX_DESCRIPTION_LENGTH = 256;

    private Long id;

    /**
     * Order of the node in the group list
     */
    @Min(0)
    private int orderIndex;

    /**
     * Domain address
     */
    @NotBlank
    @Length(max = MAX_ADDRESS_LENGTH)
    private String address;

    /**
     * Node comment
     */
    @Length(max = MAX_DESCRIPTION_LENGTH)
    private String description;

    /**
     * Owner group
     */
    @NotNull
    private ClusterGroup group;

    /**
     * Is the node active
     */
    private boolean enabled;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
