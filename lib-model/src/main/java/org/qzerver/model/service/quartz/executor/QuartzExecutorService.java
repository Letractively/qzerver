package org.qzerver.model.service.quartz.executor;

import org.qzerver.model.service.quartz.executor.dto.QuartzExecutionParameters;
import org.springframework.stereotype.Service;

@Service
public interface QuartzExecutorService {

    /**
     * Execute schedule job (initiated by Quartz trigger)
     * @param parameters Execution parameters
     */
    void executeAutomaticJob(QuartzExecutionParameters parameters);

    /**
     * Execute schedule job (inititated manually)
      * @param scheduleJobId Schedule job identifier
     */
    void executeManualJob(long scheduleJobId);

}
