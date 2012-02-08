package org.qzerver.model.service.job.impl;

import com.gainmatrix.lib.business.exception.MissingEntityException;
import com.gainmatrix.lib.time.Chronometer;
import org.qzerver.model.dao.job.ScheduleExecutionDao;
import org.qzerver.model.dao.job.ScheduleJobDao;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.cluster.ClusterGroup;
import org.qzerver.model.domain.entities.cluster.ClusterNode;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleJob;
import org.qzerver.model.service.cluster.ClusterManagementService;
import org.qzerver.model.service.job.ExecutionManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ExecutionManagementServiceImpl implements ExecutionManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionManagementServiceImpl.class);

    private ScheduleJobDao scheduleJobDao;

    private ScheduleExecutionDao scheduleExecutionDao;

    private Chronometer chronometer;

    private ClusterManagementService clusterManagementService;

    @Override
    public ScheduleExecution startExecution(long scheduleJobId, Date scheduled, Date fired) {
        ScheduleJob scheduleJob = scheduleJobDao.lockById(scheduleJobId);
        if (scheduleJob == null) {
            throw new MissingEntityException(ScheduleJob.class, scheduleJobId);
        }

        ScheduleExecution scheduleExecution = new ScheduleExecution();
        scheduleExecution.setJob(scheduleJob);
        scheduleExecution.setAction(scheduleJob.getAction());
        scheduleExecution.setCron(scheduleJob.getCron());
        scheduleExecution.setScheduled(scheduled);
        scheduleExecution.setFired(fired);
        scheduleExecution.setCancelled(false);
        scheduleExecution.setFinished(null);
        scheduleExecution.setSucceed(false);

        ClusterGroup clusterGroup = scheduleJob.getAction().getClusterGroup();
        if (clusterGroup != null) {
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

        scheduleExecutionDao.save(scheduleExecution);

        return scheduleExecution;
    }

    @Override
    public ScheduleExecutionResult startExecutionResult(long scheduleExecutionNodeId) {
        return null;
    }

    @Override
    public ScheduleExecutionResult finishExecutionResult(long scheduleExecutionResultId, ActionResult result) {
        return null;
    }

    @Override
    public ScheduleExecution finishExecution(long scheduleExecutionId, boolean succeed) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleExecution getExecution(long scheduleExecutionId) {
        return scheduleExecutionDao.findById(scheduleExecutionId);
    }

    @Override
    public void cancelExecution(long scheduleExecutionId) {
        ScheduleExecution scheduleExecution = scheduleExecutionDao.lockById(scheduleExecutionId);
        if (scheduleExecution == null) {
            throw new MissingEntityException(ScheduleExecution.class, scheduleExecutionId);
        }

        scheduleExecution.setCancelled(true);
    }

    @Required
    public ScheduleJobDao getScheduleJobDao() {
        return scheduleJobDao;
    }

    @Required
    public void setScheduleJobDao(ScheduleJobDao scheduleJobDao) {
        this.scheduleJobDao = scheduleJobDao;
    }

    @Required
    public ScheduleExecutionDao getScheduleExecutionDao() {
        return scheduleExecutionDao;
    }

    public void setScheduleExecutionDao(ScheduleExecutionDao scheduleExecutionDao) {
        this.scheduleExecutionDao = scheduleExecutionDao;
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    public void setClusterManagementService(ClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }
}
