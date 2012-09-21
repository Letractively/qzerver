package org.qzerver.model.service.quartz.management.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import org.quartz.*;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.qzerver.system.quartz.QzerverJob;
import org.qzerver.system.quartz.QzerverJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.TimeZone;

@Transactional(propagation = Propagation.REQUIRED)
public class QuartzManagementServiceImpl implements QuartzManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManagementServiceImpl.class);

    @NotNull
    private Scheduler scheduler;

    @Override
    public boolean isActive() {
        try {
            return !scheduler.isInStandbyMode();
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to check quartz activity", e);
        }
    }

    @Override
    public void setActive(boolean active) {
        LOGGER.debug("Set quartz state = {}", active);

        if (active) {
            startQuartzScheduler();
        } else {
            stopQuartzScheduler();
        }
    }

    @Override
    public void createJob(long jobId, String cron, String timeZoneId, boolean enabled) {
        JobKey jobKey = QzerverJobUtils.jobKey(jobId);

        JobDetail jobDetail = JobBuilder.newJob()
                .withIdentity(jobKey)
                .storeDurably(true)
                .requestRecovery(false)
                .ofType(QzerverJob.class)
                .build();

        try {
            scheduler.addJob(jobDetail, false);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to create quartz job", e);
        }

        if (enabled) {
            Trigger trigger = createScheduleJobTrigger(jobId, cron, timeZoneId);

            try {
                scheduler.scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to schedule quartz job on job creation", e);
            }
        }
    }

    @Override
    public void deleteJob(long jobId) {
        JobKey jobKey = QzerverJobUtils.jobKey(jobId);

        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to delete quartz job", e);
        }
    }

    @Override
    public void rescheduleJob(long jobId, String cron, String timeZoneId) {
        Trigger trigger = createScheduleJobTrigger(jobId, cron, timeZoneId);

        try {
            scheduler.rescheduleJob(trigger.getKey(), trigger);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to reschedule quartz trigger", e);
        }
    }

    @Override
    public boolean isJobEnabled(long jobId) {
        TriggerKey triggerKey = QzerverJobUtils.triggerKey(jobId);

        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger != null) {
                return true;
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to get trigger by key", e);
        }

        return false;
    }

    @Override
    public void disableJob(long jobId) {
        JobKey jobKey = QzerverJobUtils.jobKey(jobId);

        List<? extends Trigger> triggers;
        try {
            triggers = scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to get triggers list", e);
        }

        for (Trigger trigger : triggers) {
            try {
                scheduler.unscheduleJob(trigger.getKey());
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to unschedule quartz trigger", e);
            }
        }
    }

    @Override
    public void enableJob(long jobId, String cron, String timeZoneId) {
        if (isJobEnabled(jobId)) {
            return;
        }

        Trigger trigger = createScheduleJobTrigger(jobId, cron, timeZoneId);

        try {
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to schedule quartz job on job enabling", e);
        }
    }

    private Trigger createScheduleJobTrigger(long jobId, String cron, String timeZoneId) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron)
            .withMisfireHandlingInstructionDoNothing()
            .inTimeZone(timeZone);

        JobKey jobKey = QzerverJobUtils.jobKey(jobId);
        TriggerKey triggerKey = QzerverJobUtils.triggerKey(jobId);

        return TriggerBuilder.newTrigger()
            .forJob(jobKey)
            .withIdentity(triggerKey)
            .withSchedule(scheduleBuilder)
            .startNow()
            .build();
    }

    private void startQuartzScheduler() {
        try {
            if (scheduler.isInStandbyMode()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to start quartz", e);
        }
    }

    private void stopQuartzScheduler() {
        try {
            if (!scheduler.isInStandbyMode()) {
                scheduler.standby();
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to stop quartz", e);
        }
    }

    @Required
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
