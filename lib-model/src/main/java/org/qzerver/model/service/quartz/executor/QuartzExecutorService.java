package org.qzerver.model.service.quartz.executor;

import org.qzerver.model.service.quartz.executor.dto.QuartzExecutionParameters;
import org.springframework.stereotype.Service;

@Service
public interface QuartzExecutorService {

    /**
     * Execute Quartz job with the specified parameters
     * @param parameters Execution parameters
     */
    void executeJob(QuartzExecutionParameters parameters);

}
