package org.qzerver.model.service.job.management;

import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.springframework.stereotype.Service;

@Service
public interface ScheduleJobManagementService {

    /**
     * Create new scheduler job
     * @param parameters Job parameters
     * @return Job entity
     */
    ScheduleJob createJob(ScheduleJobCreateParameters parameters);

    /**
     * Modify existing job
     * @param scheduleJobId Job identifier
     * @param modifyParameters Job parameters
     * @return Job entity
     */
    ScheduleJob modifyJob(long scheduleJobId, ScheduleJobModifyParameters modifyParameters);

    /**
     * Modify existing job action
     * @param scheduleJobId Job identifier
     * @return Job entity
     */
    ScheduleJob modifyJobAction(long scheduleJobId);

    /**
     * Delete job
     * @param scheduleJobId Job identifier
     */
    void deleteJob(long scheduleJobId);


}
