package org.qzerver.system.quartz;

import com.google.common.base.Preconditions;
import org.quartz.*;
import org.qzerver.model.service.quartz.executor.QuartzExecutorService;
import org.qzerver.model.service.quartz.executor.dto.QuartzExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class QzerverJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(QzerverJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();

        LOGGER.debug("Quartz job [name={}, group={}] is fired", jobKey.getName(), jobKey.getGroup());

        if (QzerverJobUtils.QZERVER_JOB_GROUP.equals(jobKey.getGroup())) {
            ApplicationContext applicationContext = (ApplicationContext) context.get(QzerverJobListener.CONTEXT_NAME);
            Preconditions.checkNotNull(applicationContext, "Application context is not set");

            QuartzExecutorService executor = applicationContext.getBean(QuartzExecutorService.class);

            long scheduleJobId = QzerverJobUtils.parseJobName(jobKey.getName());

            QuartzExecutionParameters parameters = new QuartzExecutionParameters();
            parameters.setId(scheduleJobId);
            parameters.setFired(context.getFireTime());
            parameters.setScheduled(context.getScheduledFireTime());

            try {
                executor.executeJob(parameters);
            } catch (Exception e) {
                LOGGER.error("Fail to execute job with id : " + scheduleJobId, e);
            }
        } else {
            LOGGER.warn("Unknown Quartz job group [{}]", jobKey.getGroup());
        }
    }

}
