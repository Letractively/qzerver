package org.qzerver.model.service.job.management.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.paging.Extraction;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import com.google.common.base.Preconditions;
import org.hibernate.Hibernate;
import org.qzerver.model.dao.job.ScheduleActionDao;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.dao.job.ScheduleGroupDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobActionParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobRescheduleParameters;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ScheduleJobManagementServiceImpl implements ScheduleJobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobManagementServiceImpl.class);

    @NotNull
    private BusinessEntityDao businessEntityDao;

    @NotNull
    private ScheduleJobDao scheduleJobDao;

    @NotNull
    private ScheduleGroupDao scheduleGroupDao;

    @NotNull
    private ScheduleExecutionDao scheduleExecutionDao;

    @NotNull
    private ScheduleActionDao scheduleActionDao;

    @NotNull
    private QuartzManagementService quartzManagementService;

    @NotNull
    private Chronometer chronometer;

    @NotNull
    private Validator beanValidator;

    @Override
    public ScheduleGroup createGroup(String name) {
        Preconditions.checkNotNull(name, "Name is not specified");

        ScheduleGroup scheduleGroup = new ScheduleGroup();
        scheduleGroup.setName(name);

        businessEntityDao.save(scheduleGroup);

        return scheduleGroup;
    }

    @Override
    public void deleteGroup(long scheduleGroupId) {
        businessEntityDao.deleteById(ScheduleGroup.class, scheduleGroupId);
    }

    @Override
    public ScheduleGroup findGroup(long scheduleGroupId) {
        ScheduleGroup scheduleGroup = businessEntityDao.findById(ScheduleGroup.class, scheduleGroupId);
        if (scheduleGroup != null) {
            Hibernate.initialize(scheduleGroup.getJobs());
        }
        return scheduleGroup;
    }

    @Override
    public List<ScheduleGroup> findAllGroups(Extraction extraction) {
        return scheduleGroupDao.findAll(extraction);
    }

    @Override
    public ScheduleGroup modifyGroup(long scheduleGroupId, String name) {
        ScheduleGroup scheduleGroup = businessEntityDao.findById(ScheduleGroup.class, scheduleGroupId);
        if (scheduleGroup == null) {
            throw new MissingEntityException(ClusterGroup.class, scheduleGroupId);
        }

        scheduleGroup.setName(name);

        return scheduleGroup;
    }

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

        ScheduleGroup scheduleGroup = businessEntityDao.findById(ScheduleGroup.class, parameters.getScheduleGroupId());
        if (scheduleGroup == null) {
            throw new MissingEntityException(ScheduleGroup.class, parameters.getScheduleGroupId());
        }

        Date now = chronometer.getCurrentMoment();

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setType(parameters.getAction().getType());
        scheduleAction.setDefinition(parameters.getAction().getDefinition());
        scheduleAction.setCreated(now);
        scheduleAction.setArchived(false);

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setGroup(scheduleGroup);
        scheduleJob.setCluster(clusterGroup);
        scheduleJob.setAction(scheduleAction);
        scheduleJob.setEnabled(parameters.isEnabled());
        scheduleJob.setStandby(false);
        scheduleJob.setName(parameters.getName());
        scheduleJob.setAllNodes(parameters.isAllNodes());
        scheduleJob.setDescription(parameters.getDescription());
        scheduleJob.setCron(parameters.getCron());
        scheduleJob.setTimezone(parameters.getTimezone());
        scheduleJob.setStrategy(parameters.getStrategy());
        scheduleJob.setTrials(parameters.getTrials());
        scheduleJob.setTimeout(parameters.getTimeout());

        businessEntityDao.save(scheduleJob);

        quartzManagementService.createJob(scheduleJob.getId(),
            parameters.getCron(), parameters.getTimezone(), parameters.isEnabled());

        return scheduleJob;
    }

    @Override
    public void deleteJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleExecutionDao.detachJob(scheduleJob.getId());

        businessEntityDao.deleteById(ScheduleJob.class, scheduleJobId);

        quartzManagementService.deleteJob(scheduleJob.getId());

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
    public ScheduleJob changeJobAction(long scheduleJobId, ScheduleJobActionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleActionDao.removeOrphanedActions();

        Date now = chronometer.getCurrentMoment();

        ScheduleAction previousScheduleAction = scheduleJob.getAction();
        previousScheduleAction.setArchived(true);

        ScheduleAction scheduleAction = new ScheduleAction();
        scheduleAction.setType(parameters.getType());
        scheduleAction.setDefinition(parameters.getDefinition());
        scheduleAction.setCreated(now);
        scheduleAction.setArchived(false);

        scheduleJob.setAction(scheduleAction);

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
        scheduleJob.setTimezone(parameters.getTimezone());

        if (scheduleJob.isEnabled()) {
            quartzManagementService.rescheduleJob(scheduleJob.getId(), scheduleJob.getCron(), parameters.getTimezone());
        }

        return scheduleJob;
    }

    @Override
    public ScheduleJob enableJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        if (scheduleJob.isEnabled()) {
            return scheduleJob;
        }

        scheduleJob.setEnabled(true);

        quartzManagementService.enableJob(scheduleJob.getId(), scheduleJob.getCron(), scheduleJob.getTimezone());

        return scheduleJob;
    }

    @Override
    public ScheduleJob disableJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        if (!scheduleJob.isEnabled()) {
            return scheduleJob;
        }

        scheduleJob.setEnabled(false);

        quartzManagementService.disableJob(scheduleJob.getId());

        return scheduleJob;
    }

    @Override
    public ScheduleJob findJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            return null;
        }

        Hibernate.initialize(scheduleJob.getAction());
        Hibernate.initialize(scheduleJob.getGroup());
        Hibernate.initialize(scheduleJob.getCluster());

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
    public void setQuartzManagementService(QuartzManagementService quartzManagementService) {
        this.quartzManagementService = quartzManagementService;
    }

    @Required
    public void setScheduleGroupDao(ScheduleGroupDao scheduleGroupDao) {
        this.scheduleGroupDao = scheduleGroupDao;
    }

    @Required
    public void setScheduleActionDao(ScheduleActionDao scheduleActionDao) {
        this.scheduleActionDao = scheduleActionDao;
    }
}
