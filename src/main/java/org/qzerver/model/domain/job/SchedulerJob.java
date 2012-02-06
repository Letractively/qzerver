package org.qzerver.model.domain.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

public class SchedulerJob extends AbstractBusinessEntity<Long> {

    public static final int MAX_NAME_LENGTH = 128;

    public static final int MAX_DESCRIPTION_LENGTH = 512;

    private Long id;

    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    @Length(min = 1, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    public SchedulerJob() {
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
}
