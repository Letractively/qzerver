package org.qzerver.model.service.job.executor;

import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.springframework.stereotype.Service;

@Service
public interface ScheduleJobExecutorService {

    /**
     * Execute schedule job (initiated by Quartz trigger)
     * @param parameters Execution parameters
     * @return Status of execution
     */
    ScheduleExecution executeAutomaticJob(AutomaticJobExecutionParameters parameters);

    /**
     * Execute schedule job (inititated manually)
      * @param scheduleJobId Schedule job identifier
     * @return Status of execution
     */
    ScheduleExecution executeManualJob(long scheduleJobId);

}
