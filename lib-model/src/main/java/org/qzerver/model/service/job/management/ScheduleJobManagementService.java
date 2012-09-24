package org.qzerver.model.service.job.management;

import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.springframework.stereotype.Service;

/**
 * Schedule job management
 */
@Service
public interface ScheduleJobManagementService {

    /**
     * Create new scheduler job
     * @param parameters Job creation parameters
     * @return Job entity
     */
    ScheduleJob createJob(ScheduleJobCreateParameters parameters);

    /**
     * Modify existing job
     * @param scheduleJobId Job identifier
     * @param parameters Job modification parameters
     * @return Job entity
     */
    ScheduleJob modifyJob(long scheduleJobId, ScheduleJobModifyParameters parameters);

    /**
     * Reschedule job
     * @param scheduleJobId Job identifier
     * @param parameters Job reschedule parameters
     * @return Job entity
     */
    ScheduleJob rescheduleJob(long scheduleJobId, ScheduleJobRescheduleParameters parameters);

    /**
     * Enable job
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob enableJob(long scheduleJobId);

    /**
     * Disable job
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob disableJob(long scheduleJobId);

    /**
     * Get job description
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob findJob(long scheduleJobId);

    /**
     * Delete job
     * @param scheduleJobId Job identifier
     */
    void deleteJob(long scheduleJobId);

}
