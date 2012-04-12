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
     * Control job
     * @param scheduleJobId Job identifier
     * @param enabled Whether job is enabled
     * @return Job entity
     */
    ScheduleJob controlJob(long scheduleJobId, boolean enabled);

    /**
     * Delete job
     * @param scheduleJobId Job identifier
     */
    void deleteJob(long scheduleJobId);

}
