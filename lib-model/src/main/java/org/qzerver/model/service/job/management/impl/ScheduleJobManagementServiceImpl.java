package org.qzerver.model.service.job.management.impl;

import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleGroup;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.job.management.ScheduleJobManagementService;
import org.qzerver.model.service.job.management.dto.ScheduleJobCreateParameters;
import org.qzerver.model.service.job.management.dto.ScheduleJobModifyParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Date;

@Transactional(propagation = Propagation.REQUIRED)
public class ScheduleJobManagementServiceImpl implements ScheduleJobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobManagementServiceImpl.class);

    private BusinessEntityDao businessEntityDao;

    private ScheduleJobDao scheduleJobDao;

    private Chronometer chronometer;

    private Validator beanValidator;

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
        scheduleAction.setDefinition("<xml></xml>");
        scheduleAction.setType(parameters.getActionType());
        scheduleAction.setCreated(now);
        scheduleAction.setArchived(false);

        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setGroup(scheduleGroup);
        scheduleJob.setCluster(clusterGroup);
        scheduleJob.setAction(scheduleAction);
        scheduleJob.setEnabled(true);
        scheduleJob.setStandby(false);
        scheduleJob.setConcurrent(true);
        scheduleJob.setName(parameters.getName());
        scheduleJob.setDescription(parameters.getDescription());
        scheduleJob.setCron(parameters.getCron());

        businessEntityDao.save(scheduleJob);

        return scheduleJob;
    }

    @Override
    public void deleteJob(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        ScheduleGroup scheduleGroup = scheduleJob.getGroup();
        scheduleGroup.getJobs().remove(scheduleJob);

        if (scheduleGroup.getJobs().size() == 0) {
            businessEntityDao.delete(scheduleGroup);
        }

        businessEntityDao.deleteById(ScheduleJob.class, scheduleJobId);
    }

    @Override
    public ScheduleJob modifyJob(long scheduleJobId, ScheduleJobModifyParameters modifyParameters) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleJob.setName(scheduleJob.getName());

        return scheduleJob;
    }

    @Override
    public ScheduleJob modifyJobAction(long scheduleJobId) {
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        scheduleJob.setName(scheduleJob.getName());

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
}
