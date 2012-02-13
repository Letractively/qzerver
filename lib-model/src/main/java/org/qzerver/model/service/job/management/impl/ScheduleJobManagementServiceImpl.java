package org.qzerver.model.service.job.management.impl;

import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.quartz.*;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.qzerver.system.quartz.QzerverJob;
import org.qzerver.system.quartz.QzerverJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Transactional(propagation = Propagation.REQUIRED)
public class ScheduleJobManagementServiceImpl implements ScheduleJobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobManagementServiceImpl.class);

    private BusinessEntityDao businessEntityDao;

    private ScheduleJobDao scheduleJobDao;

    private ScheduleExecutionDao scheduleExecutionDao;

    private Chronometer chronometer;

    private Validator beanValidator;

    private Scheduler scheduler;

    @Override
    public ScheduleJob createJob(ScheduleJobCreateParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        ClusterGroup clusterGroup = null;
        if (parameters.getClusterGroupId() != null) {
            clusterGroup = businessEntityDao.findById(ClusterGroup.class, parameters.getClusterGroupId());
            if (clusterGroup == null) {
                throw new MissingEntityException(ClusterGroup.class, parameters.getClusterGroupId());
            }
        }

        ScheduleGroup scheduleGroup = businessEntityDao.findById(ScheduleGroup.class, parameters.getSchedulerGroupId());
        if (scheduleGroup == null) {
            throw new MissingEntityException(ScheduleGroup.class, parameters.getSchedulerGroupId());
        }

        Date now = chronometer.getCurrentMoment();

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setDefinition("<xml></xml>".getBytes());
        scheduleAction.setType(parameters.getActionType());
        scheduleAction.setCreated(now);
        scheduleAction.setArchived(false);

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setGroup(scheduleGroup);
        scheduleJob.setCluster(clusterGroup);
        scheduleJob.setAction(scheduleAction);
        scheduleJob.setEnabled(parameters.isEnabled());
        scheduleJob.setStandby(false);
        scheduleJob.setConcurrent(false);
        scheduleJob.setName(parameters.getName());
        scheduleJob.setDescription(parameters.getDescription());
        scheduleJob.setCron(parameters.getCron());
        scheduleJob.setTimezone(parameters.getTimezone());

        businessEntityDao.save(scheduleJob);

        JobDetail jobDetail = JobBuilder.newJob()
                .withIdentity(QzerverJobUtils.jobKey(scheduleJob))
                .storeDurably(true)
                .requestRecovery(false)
                .ofType(QzerverJob.class)
                .build();

        try {
            scheduler.addJob(jobDetail, false);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to create quartz job", e);
        }

        if (scheduleJob.isEnabled()) {
            TimeZone timeZone = TimeZone.getTimeZone(parameters.getTimezone());

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
                    .withMisfireHandlingInstructionDoNothing()
                    .inTimeZone(timeZone);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(QzerverJobUtils.jobKey(scheduleJob))
                    .withIdentity(QzerverJobUtils.triggerKey(scheduleJob))
                    .withSchedule(scheduleBuilder)
                    .startNow()
                    .build();

            try {
                scheduler.scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to schedule quartz job", e);
            }
        }

        return scheduleJob;
    }

    @Override
    public void deleteJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleExecutionDao.detachJob(scheduleJob.getId());

        try {
            scheduler.deleteJob(QzerverJobUtils.jobKey(scheduleJob));
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to delete quartz job", e);
        }

        businessEntityDao.deleteById(ScheduleJob.class, scheduleJobId);

        ScheduleGroup scheduleGroup = scheduleJob.getGroup();
        scheduleGroup.getJobs().remove(scheduleJob);

        if (scheduleGroup.getJobs().size() == 0) {
            businessEntityDao.delete(scheduleGroup);
        }
    }

    @Override
    public ScheduleJob modifyJob(long scheduleJobId, ScheduleJobModifyParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleJob.setName(parameters.getName());
        scheduleJob.setDescription(parameters.getDescription());

        return scheduleJob;
    }

    @Override
    public ScheduleJob rescheduleJob(long scheduleJobId, ScheduleJobRescheduleParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleJob.setCron(parameters.getCron());

        TimeZone timeZone = TimeZone.getTimeZone(parameters.getTimezone());

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
                .withMisfireHandlingInstructionDoNothing()
                .inTimeZone(timeZone);

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .forJob(QzerverJobUtils.jobKey(scheduleJob))
                .withIdentity(QzerverJobUtils.triggerKey(scheduleJob))
                .withSchedule(scheduleBuilder)
                .startNow()
                .build();

        try {
            scheduler.rescheduleJob(QzerverJobUtils.triggerKey(scheduleJob), trigger);
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to reschedule quartz trigger", e);
        }

        return scheduleJob;
    }

    @Override
    public ScheduleJob controlJob(long scheduleJobId, boolean enabled) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        if (scheduleJob.isEnabled() == enabled) {
            return scheduleJob;
        }

        scheduleJob.setEnabled(enabled);

        if (enabled) {
            TimeZone timeZone = TimeZone.getTimeZone(scheduleJob.getTimezone());

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
                    .withMisfireHandlingInstructionDoNothing()
                    .inTimeZone(timeZone);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .forJob(QzerverJobUtils.jobKey(scheduleJob))
                    .withIdentity(QzerverJobUtils.triggerKey(scheduleJob))
                    .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCron()))
                    .startNow()
                    .build();

            try {
                scheduler.scheduleJob(trigger);
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to schedule quartz trigger", e);
            }
        } else {
            List<? extends Trigger> triggers;
            try {
                triggers = scheduler.getTriggersOfJob(QzerverJobUtils.jobKey(scheduleJob));
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

        return scheduleJob;
    }

    @Required
    public void setBusinessEntityDao(BusinessEntityDao businessEntityDao) {
        this.businessEntityDao = businessEntityDao;
    }

    @Required
    public void setScheduleJobDao(ScheduleJobDao scheduleJobDao) {
        this.scheduleJobDao = scheduleJobDao;
    }

    @Required
    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

    @Required
    public void setScheduleExecutionDao(ScheduleExecutionDao scheduleExecutionDao) {
        this.scheduleExecutionDao = scheduleExecutionDao;
    }

    @Required
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
