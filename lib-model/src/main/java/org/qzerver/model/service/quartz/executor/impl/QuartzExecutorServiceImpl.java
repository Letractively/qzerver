package org.qzerver.model.service.quartz.executor.impl;

import org.qzerver.model.service.quartz.executor.QuartzExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class QuartzExecutorServiceImpl implements QuartzExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzExecutorServiceImpl.class);

    @Override
    public void executeJob(String quartzJobName, String quartzJobGroup) {
        LOGGER.info("Quartz job [{}] is fired", quartzJobName);
    }

}
