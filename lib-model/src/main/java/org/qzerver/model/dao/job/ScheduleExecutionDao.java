package org.qzerver.model.dao.job;

import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleExecutionDao extends BusinessEntityDao<ScheduleExecution, Long> {

    /**
     * Find all last executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findAll(Extraction extraction);

    /**
     * Find all last closed executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findFinished(Extraction extraction);

    /**
     * Find all last executions in progress
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findEngaged(Extraction extraction);

    /**
     * Find last executions by job
     * @param scheduleJobId Schedule job identifier
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> findByJob(long scheduleJobId, Extraction extraction);

}
