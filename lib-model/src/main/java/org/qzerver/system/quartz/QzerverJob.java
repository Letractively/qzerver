package org.qzerver.system.quartz;

import com.google.common.base.Preconditions;
import org.quartz.*;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class QzerverJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(QzerverJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Preconditions.checkNotNull(context, "Context is not set");

        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();

        LOGGER.debug("Quartz job [name={}, group={}] is fired", jobKey.getName(), jobKey.getGroup());

        if (!QzerverJobUtils.isQzerverJob(jobKey)) {
            LOGGER.warn("Unknown job [name={}, group={}] is fired", jobKey.getName(), jobKey.getGroup());
            return;
        }

        ScheduleJobExecutorService executorService =
            (ScheduleJobExecutorService) context.get(QzerverJobListener.SERVICE_NAME);
        Preconditions.checkNotNull(executorService, "Executor service is not set");

        long scheduleJobId = QzerverJobUtils.parseJobName(jobKey.getName());

        AutomaticJobExecutionParameters parameters = new AutomaticJobExecutionParameters();
        parameters.setScheduleJobId(scheduleJobId);
        parameters.setFired(context.getFireTime());
        parameters.setScheduled(context.getScheduledFireTime());

        try {
            executorService.executeAutomaticJob(parameters);
        } catch (Exception e) {
            LOGGER.error("Fail to execute job with id : " + scheduleJobId, e);
        }
    }

}
