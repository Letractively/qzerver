package org.qzerver.model.service.job.management.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.entities.job.ScheduleJob;

import java.io.Serializable;

public class ScheduleJobModifyParameters implements Serializable {

    @NotBlank
    @Length(max = ScheduleJob.MAX_NAME_LENGTH)
    private String name;

    @Length(max = ScheduleJob.MAX_DESCRIPTION_LENGTH)
    private String description;


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
