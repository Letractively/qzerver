package org.qzerver.model.service.job.execution.impl;

import com.gainmatrix.lib.business.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.apache.commons.lang.StringUtils;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartJobExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ScheduleExecutionManagementServiceImpl implements ScheduleExecutionManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleExecutionManagementServiceImpl.class);

    private ScheduleExecutionDao scheduleExecutionDao;

    private Chronometer chronometer;

    private ClusterManagementService clusterManagementService;

    private BusinessEntityDao businessEntityDao;

    private String node;

    private Validator beanValidator;

    @Override
    public ScheduleExecution startExecution(StartJobExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Start execution of job [id={}]", parameters.getScheduleJobId());

        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, parameters.getScheduleJobId());
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, parameters.getScheduleJobId());
        }

        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setJob(scheduleJob);
        scheduleExecution.setAction(scheduleJob.getAction());
        scheduleExecution.setCron(scheduleJob.getCron());
        scheduleExecution.setName(scheduleJob.getName());
        scheduleExecution.setScheduled(parameters.getScheduled());
        scheduleExecution.setFired(parameters.getFired());
        scheduleExecution.setCancelled(false);
        scheduleExecution.setFinished(null);
        scheduleExecution.setSucceed(false);
        scheduleExecution.setHostname(StringUtils.left(node, ScheduleExecution.MAX_NODE_LENGTH));

        ClusterGroup clusterGroup = scheduleJob.getClusterGroup();
        if ((clusterGroup != null) && (! scheduleJob.getAction().getType().isLocal())) {
            List<ClusterNode> clusterNodes = new ArrayList<ClusterNode>(clusterGroup.getNodes().size());

            switch (clusterGroup.getStrategy()) {
                // "line" strategy - always start from the first active node
                case LINE:
                    for (ClusterNode clusterNode : clusterGroup.getNodes()) {
                        if (clusterNode.isActive()) {
                            clusterNodes.add(clusterNode);
                        }
                    }
                    break;
                // "random" strategy - choose active nodes in random order
                case RANDOM:
                    for (ClusterNode clusterNode : clusterGroup.getNodes()) {
                        if (clusterNode.isActive()) {
                            clusterNodes.add(clusterNode);
                        }
                    }
                    Collections.shuffle(clusterNodes);
                    break;
                // "circle" strategy - step index in cluster and get all active nodes
                case CIRCLE:
                    int rolledIndex = clusterManagementService.rollGroupIndex(clusterGroup.getId());
                    for (int i=rolledIndex, size=clusterGroup.getNodes().size(); i < size; i++) {
                        ClusterNode clusterNode = clusterGroup.getNodes().get(i);
                        if (clusterNode.isActive()) {
                            clusterNodes.add(clusterNode);
                        }
                    }
                    for (int i=0; i < rolledIndex; i++) {
                        ClusterNode clusterNode = clusterGroup.getNodes().get(i);
                        if (clusterNode.isActive()) {
                            clusterNodes.add(clusterNode);
                        }
                    }
                    break;
                // if strategy is unknown - do nothing and no any node is added
                default:
                    break;
            }

            for (ClusterNode clusterNode : clusterNodes) {
                ScheduleExecutionNode executionNode = new ScheduleExecutionNode();
                executionNode.setLocalhost(false);
                executionNode.setDomain(clusterNode.getDomain());
                executionNode.setExecution(scheduleExecution);
                scheduleExecution.getNodes().add(executionNode);
            }
        } else {
            ScheduleExecutionNode executionNode = new ScheduleExecutionNode();
            executionNode.setLocalhost(true);
            executionNode.setDomain("localhost");
            executionNode.setExecution(scheduleExecution);
            scheduleExecution.getNodes().add(executionNode);
        }

        businessEntityDao.save(scheduleExecution);

        return scheduleExecution;
    }

    @Override
    public ScheduleExecutionResult startExecutionResult(long scheduleExecutionNodeId) {
        ScheduleExecutionNode node = businessEntityDao.findById(ScheduleExecutionNode.class, scheduleExecutionNodeId);
        if (node == null) {
            throw new MissingEntityException(ScheduleExecutionNode.class, scheduleExecutionNodeId);
        }

        ScheduleExecution execution = node.getExecution();
        businessEntityDao.lock(execution);

        ScheduleExecutionResult result = new ScheduleExecutionResult();
        result.setStarted(chronometer.getCurrentMoment());
        result.setFinished(null);
        result.setSucceed(false);

        result.setNode(node);
        node.setResult(result);

        result.setExecution(execution);
        execution.getResults().add(result);

        businessEntityDao.save(result);

        return result;
    }

    @Override
    public ScheduleExecutionResult finishExecutionResult(long scheduleExecutionResultId, ActionResult actionResult) {
        ScheduleExecutionResult result = businessEntityDao.findById(ScheduleExecutionResult.class, scheduleExecutionResultId);
        if (result == null) {
            throw new MissingEntityException(ScheduleExecutionResult.class, scheduleExecutionResultId);
        }

        result.setFinished(chronometer.getCurrentMoment());
        result.setSucceed(actionResult.isSucceed());
        result.setResult("<xml></xml>");

        return result;
    }

    @Override
    public ScheduleExecution finishExecution(long scheduleExecutionId, boolean succeed) {
        ScheduleExecution scheduleExecution = businessEntityDao.lockById(ScheduleExecution.class, scheduleExecutionId);
        if (scheduleExecution == null) {
            throw new MissingEntityException(ScheduleExecution.class, scheduleExecutionId);
        }

        scheduleExecution.setFinished(chronometer.getCurrentMoment());
        scheduleExecution.setSucceed(succeed);

        return scheduleExecution;
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleExecution getExecution(long scheduleExecutionId) {
        return businessEntityDao.findById(ScheduleExecution.class, scheduleExecutionId);
    }

    @Override
    public void cancelExecution(long scheduleExecutionId) {
        ScheduleExecution scheduleExecution = businessEntityDao.lockById(ScheduleExecution.class, scheduleExecutionId);
        if (scheduleExecution == null) {
            throw new MissingEntityException(ScheduleExecution.class, scheduleExecutionId);
        }

        scheduleExecution.setCancelled(true);
    }

    @Required
    public void setScheduleExecutionDao(ScheduleExecutionDao scheduleExecutionDao) {
        this.scheduleExecutionDao = scheduleExecutionDao;
    }

    @Required
    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    @Required
    public void setClusterManagementService(ClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    @Required
    public void setNode(String node) {
        this.node = node;
    }

    @Required
    public void setBusinessEntityDao(BusinessEntityDao businessEntityDao) {
        this.businessEntityDao = businessEntityDao;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
