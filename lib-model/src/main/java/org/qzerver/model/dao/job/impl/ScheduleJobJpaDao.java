package org.qzerver.model.dao.job.impl;

import org.qzerver.model.dao.business.AbstractBusinessEntityDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public class ScheduleJobJpaDao extends AbstractBusinessEntityDao<ScheduleJob, Long> implements ScheduleJobDao {

    public ScheduleJobJpaDao() {
        super(ScheduleJob.class);
    }
}
