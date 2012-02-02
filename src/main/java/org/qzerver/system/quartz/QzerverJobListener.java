package org.qzerver.system.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

public class QzerverJobListener extends JobListenerSupport {

    private static final String LISTENER_NAME = "QZERVER job listener";

    private ApplicationContext applicationContext;

    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.put("ApplicationContext", applicationContext);
    }

    @Resource
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
