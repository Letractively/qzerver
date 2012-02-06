package org.qzerver.model.service.quartz.impl;

import org.quartz.Scheduler;
import org.qzerver.model.service.quartz.QuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class QuartzServiceImpl implements QuartzService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzServiceImpl.class);

    private Scheduler scheduler;

    @Override
    public void executeJob(String quartzJobName, String quartzJobGroup) {
        LOGGER.info("Quartz job [{}] is fired", quartzJobName);
    }

    @Required
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
