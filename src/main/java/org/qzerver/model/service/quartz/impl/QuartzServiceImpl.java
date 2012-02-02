package org.qzerver.model.service.quartz.impl;

import org.qzerver.model.service.quartz.QuartzService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class QuartzServiceImpl implements QuartzService {

    @Override
    public void executeJob(String jobId) {
        System.out.println("wrgqwrgqwrgwr " + jobId);
    }

}
