package org.qzerver.model.dao.job;

import com.gainmatrix.lib.business.BusinessEntityDao;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleJobDao extends BusinessEntityDao<ScheduleJob, Long> {
}
