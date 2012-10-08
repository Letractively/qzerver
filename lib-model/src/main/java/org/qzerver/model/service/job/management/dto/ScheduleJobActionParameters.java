package org.qzerver.model.service.job.management.dto;

import org.hibernate.validator.constraints.Length;
import org.qzerver.model.domain.entities.job.ScheduleAction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class ScheduleJobActionParameters implements Serializable {

    @NotNull
    @Length(max = ScheduleAction.MAX_TYPE_LENGTH)
    private String type;

    @Size(max = ScheduleAction.MAX_DEFINITION_LENGTH)
    private byte[] definition;

    public byte[] getDefinition() {
        return definition;
    }

    public void setDefinition(byte[] definition) {
        this.definition = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
