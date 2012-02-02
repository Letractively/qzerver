package org.qzerver.system.quartz;

import com.google.common.base.Preconditions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class QzerverJob implements Job {

    private ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Preconditions.checkNotNull(applicationContext, "Application context is not set");

        QzerverJobExecutor executor = applicationContext.getBean(QzerverJobExecutor.class);
        executor.executeJob(context.getJobDetail().getKey().getName());
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
