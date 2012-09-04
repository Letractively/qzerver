package org.qzerver.model.service.job.execution.impl;

import com.gainmatrix.lib.business.entity.BusinessEntityDao;
import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.paging.Extraction;
import com.gainmatrix.lib.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.*;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ScheduleExecutionManagementServiceImpl implements ScheduleExecutionManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleExecutionManagementServiceImpl.class);

    @NotNull
    private ScheduleExecutionDao scheduleExecutionDao;

    @NotNull
    private Chronometer chronometer;

    @NotNull
    private ClusterManagementService clusterManagementService;

    @NotNull
    private BusinessEntityDao businessEntityDao;

    @NotNull
    private String node;

    @NotNull
    private Validator beanValidator;

    @Override
    public ScheduleExecution startExecution(StartExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Start execution of job [id={}]", parameters.getScheduleJobId());

        Date now = chronometer.getCurrentMoment();

        // Select job by id
        ScheduleJob scheduleJob = businessEntityDao.lockById(ScheduleJob.class, parameters.getScheduleJobId());
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, parameters.getScheduleJobId());
        }

        // Create new execution
        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setJob(scheduleJob);
        scheduleExecution.setAction(scheduleJob.getAction());
        scheduleExecution.setCron(scheduleJob.getCron());
        scheduleExecution.setName(scheduleJob.getName());
        scheduleExecution.setStrategy(scheduleJob.getStrategy());
        scheduleExecution.setTimeout(scheduleJob.getTimeout());
        scheduleExecution.setAllNodes(scheduleJob.isAllNodes());
        scheduleExecution.setScheduled(parameters.getScheduled());
        scheduleExecution.setFired(parameters.getFired());
        scheduleExecution.setForced(parameters.isManual());
        scheduleExecution.setStatus(ScheduleExecutionStatus.SUCCEED);
        scheduleExecution.setStarted(now);
        scheduleExecution.setFinished(null);

        String hostName = StringUtils.left(node, ScheduleExecution.MAX_NODE_LENGTH);
        scheduleExecution.setHostname(hostName);

        // Check if cluster group is specified for the job
        ClusterGroup clusterGroup = scheduleJob.getCluster();
        if (clusterGroup != null) {
            // If cluster is set then choose some nodes to work on
            List<ClusterNode> selectedNodes = selectJobNodes(scheduleJob);
            for (ClusterNode clusterNode : selectedNodes) {
                ScheduleExecutionNode executionNode = new ScheduleExecutionNode();
                executionNode.setLocalhost(false);
                executionNode.setAddress(clusterNode.getAddress());
                executionNode.setExecution(scheduleExecution);
                scheduleExecution.getNodes().add(executionNode);
            }
        } else {
            // If cluster is not set then the only node is 'localhost' node
            ScheduleExecutionNode executionNode = new ScheduleExecutionNode();
            executionNode.setLocalhost(true);
            executionNode.setAddress("localhost");
            executionNode.setExecution(scheduleExecution);
            scheduleExecution.getNodes().add(executionNode);
        }

        // Save created execution entity
        businessEntityDao.save(scheduleExecution);

        return scheduleExecution;
    }

    private List<ClusterNode> selectJobNodes(ScheduleJob scheduleJob) {
        ClusterGroup clusterGroup = scheduleJob.getCluster();

        List<ClusterNode> selectedNodes;

        // Choose nodes depending on the selection strategy
        switch (scheduleJob.getStrategy()) {
            case INDEXED:
                selectedNodes = selectJobNodesInIndexedOrder(clusterGroup);
                break;
            case RANDOM:
                selectedNodes = selectJobNodesInRandomOrder(clusterGroup);
                break;
            case CIRCULAR:
                selectedNodes = selectJobNodesInCircularOrder(clusterGroup);
                break;
            default:
                throw new IllegalStateException("Unknown strategy: " + scheduleJob.getStrategy());
        }

        // Limit node count if trial count is set
        if (scheduleJob.getTrials() > 0) {
            if (scheduleJob.getTrials() < selectedNodes.size()) {
                selectedNodes = selectedNodes.subList(0, scheduleJob.getTrials());
            }
        }

        return selectedNodes;
    }

    private List<ClusterNode> selectJobNodesInIndexedOrder(ClusterGroup clusterGroup) {
        List<ClusterNode> selectedNodes = new ArrayList<ClusterNode>(clusterGroup.getNodes().size());

        // "line" strategy - always start from the first active node
        for (ClusterNode clusterNode : clusterGroup.getNodes()) {
            if (clusterNode.isEnabled()) {
                selectedNodes.add(clusterNode);
            }
        }

        return selectedNodes;
    }

    private List<ClusterNode> selectJobNodesInRandomOrder(ClusterGroup clusterGroup) {
        List<ClusterNode> selectedNodes = new ArrayList<ClusterNode>(clusterGroup.getNodes().size());

        // "random" strategy - choose active nodes in random order
        for (ClusterNode clusterNode : clusterGroup.getNodes()) {
            if (clusterNode.isEnabled()) {
                selectedNodes.add(clusterNode);
            }
        }

        Collections.shuffle(selectedNodes);

        return selectedNodes;
    }

    private List<ClusterNode> selectJobNodesInCircularOrder(ClusterGroup clusterGroup) {
        List<ClusterNode> selectedNodes = new ArrayList<ClusterNode>(clusterGroup.getNodes().size());

        // "circle" strategy - step index in cluster and get all active nodes
        int rolledIndex = clusterManagementService.rollGroupIndex(clusterGroup.getId());

        for (int i = rolledIndex, size = clusterGroup.getNodes().size(); i < size; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isEnabled()) {
                selectedNodes.add(clusterNode);
            }
        }

        for (int i = 0; i < rolledIndex; i++) {
            ClusterNode clusterNode = clusterGroup.getNodes().get(i);
            if (clusterNode.isEnabled()) {
                selectedNodes.add(clusterNode);
            }
        }

        return selectedNodes;
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
        ScheduleExecutionResult result =
            businessEntityDao.findById(ScheduleExecutionResult.class, scheduleExecutionResultId);

        if (result == null) {
            throw new MissingEntityException(ScheduleExecutionResult.class, scheduleExecutionResultId);
        }

        result.setFinished(chronometer.getCurrentMoment());

        if (actionResult != null) {
            result.setSucceed(actionResult.isSucceed());
            result.setPayload("<xml></xml>".getBytes());
        } else {
            result.setSucceed(false);
            result.setPayload(null);
        }

        return result;
    }

    @Override
    public ScheduleExecution finishExecution(long scheduleExecutionId, ScheduleExecutionStatus status) {
        Preconditions.checkNotNull(status);

        ScheduleExecution scheduleExecution = businessEntityDao.lockById(ScheduleExecution.class, scheduleExecutionId);
        if (scheduleExecution == null) {
            throw new MissingEntityException(ScheduleExecution.class, scheduleExecutionId);
        }

        scheduleExecution.setFinished(chronometer.getCurrentMoment());
        scheduleExecution.setStatus(status);

        scheduleExecution.getResults().size();
        scheduleExecution.getNodes().size();
        scheduleExecution.getJob().getVersion();
        scheduleExecution.getAction().getVersion();

        return scheduleExecution;
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleExecution getExecution(long scheduleExecutionId) {
        ScheduleExecution scheduleExecution = businessEntityDao.findById(ScheduleExecution.class, scheduleExecutionId);
        scheduleExecution.getResults().size();
        scheduleExecution.getNodes().size();
        scheduleExecution.getAction().getVersion();
        return scheduleExecution;
    }

    @Override
    public void cancelExecution(long scheduleExecutionId) {
        ScheduleExecution scheduleExecution = businessEntityDao.lockById(ScheduleExecution.class, scheduleExecutionId);
        if (scheduleExecution == null) {
            throw new MissingEntityException(ScheduleExecution.class, scheduleExecutionId);
        }

        scheduleExecution.setCancelled(true);
    }

    @Override
    public List<ScheduleExecution> getAll(Extraction extraction) {
        return scheduleExecutionDao.findAll(extraction);
    }

    @Override
    public List<ScheduleExecution> getByJob(long scheduleJobId, Extraction extraction) {
        return scheduleExecutionDao.findByJob(scheduleJobId, extraction);
    }

    @Override
    public List<ScheduleExecution> getEngaged(Extraction extraction) {
        return scheduleExecutionDao.findEngaged(extraction);
    }

    @Override
    public List<ScheduleExecution> getFinished(Extraction extraction) {
        return scheduleExecutionDao.findFinished(extraction);
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
