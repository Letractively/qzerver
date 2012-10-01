package org.qzerver.model.service.job.executor.dto;

import org.hibernate.validator.constraints.Length;
import org.qzerver.model.domain.entities.job.ScheduleExecution;

import java.io.Serializable;
import java.util.List;

public class ManualJobExecutionParameters implements Serializable {

    @Length(max = ScheduleExecution.MAX_COMMENT_LENGTH)
    private String comment;

    private List<String> addresses;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
}
