package org.qzerver.model.domain.job;

import com.gainmatrix.lib.business.AbstractBusinessEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.domain.business.BusinessModel;

import java.util.HashSet;
import java.util.Set;

public class ScheduleGroup extends AbstractBusinessEntity<Long> {

    public static final int MAX_NAME_LENGTH = 128;

    private Long id;

    @NotBlank
    @Length(max = MAX_NAME_LENGTH)
    private String name;

    private Set<ScheduleJob> jobs;

    public ScheduleGroup() {
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

    public Set<ScheduleJob> getJobs() {
        if (jobs == null) {
            jobs = new HashSet<ScheduleJob>();
        }
        return jobs;
    }

    public void setJobs(Set<ScheduleJob> jobs) {
        this.jobs = jobs;
    }
}

