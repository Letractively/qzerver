package org.qzerver.system.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

public class QzerverJobListener extends JobListenerSupport {

    protected static final String CONTEXT_NAME = "QzerverApplicationContext";

    private static final String LISTENER_NAME = "QZERVER job listener";

    private ApplicationContext applicationContext;

    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.put(CONTEXT_NAME, applicationContext);
    }

    @Resource
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
