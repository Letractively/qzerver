package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.business.exception.AbstractServiceException;
import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import com.gainmatrix.lib.time.ChronometerTimer;
import org.apache.commons.collections.CollectionUtils;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.agent.action.ActionAgentResult;
import org.qzerver.model.domain.entities.job.*;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.qzerver.model.service.job.executor.dto.ManualJobExecutionParameters;
import org.qzerver.model.service.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Iterator;

@Transactional(propagation = Propagation.NEVER)
public class ScheduleJobExecutorServiceImpl implements ScheduleJobExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobExecutorServiceImpl.class);

    @NotNull
    private Validator beanValidator;

    @NotNull
    private ScheduleExecutionManagementService executionManagementService;

    @NotNull
    private Chronometer chronometer;

    @NotNull
    private ActionAgent actionAgent;

    @NotNull
    private MailService mailService;

    @Override
    public ScheduleExecution executeAutomaticJob(long scheduleJobId, AutomaticJobExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] will be executed (auto)", scheduleJobId);

        StartExecutionParameters executionParameters = new StartExecutionParameters();
        executionParameters.setScheduled(parameters.getScheduledTime());
        executionParameters.setFired(parameters.getFiredTime());
        executionParameters.setManual(false);
        executionParameters.setComment(null);
        executionParameters.setAddresses(null);

        ScheduleExecution scheduleExecution = executeJob(scheduleJobId, executionParameters);

        if (scheduleExecution.getStatus() != ScheduleExecutionStatus.SUCCEED) {
            ScheduleJob scheduleJob = scheduleExecution.getJob();
            if (scheduleJob.isNotifyOnFailure()) {
                mailService.notifyJobExecutionFailed(scheduleExecution);
            }
        }

        return scheduleExecution;
    }

    @Override
    public ScheduleExecution executeManualJob(long scheduleJobId, ManualJobExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] will be executed (manual)", scheduleJobId);

        Date now = chronometer.getCurrentMoment();

        StartExecutionParameters executionParameters = new StartExecutionParameters();
        executionParameters.setScheduled(now);
        executionParameters.setFired(now);
        executionParameters.setManual(true);
        executionParameters.setComment(parameters.getComment());
        executionParameters.setAddresses(parameters.getAddresses());

        return executeJob(scheduleJobId, executionParameters);
    }

    protected ScheduleExecution executeJob(long scheduleJobId, StartExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        // Compose execution descriptor
        ScheduleExecution scheduleExecution = executionManagementService.startExecution(scheduleJobId, parameters);

        // The only reason the status is not assigned is that exception occurs in executeJobNodes() call
        ScheduleExecutionStatus status = ScheduleExecutionStatus.EXCEPTION;

        // Try to execute action on a node from the cluster
        try {
            try {
                status = executeJobNodes(scheduleExecution);
            } catch (Exception e) {
                LOGGER.error("Internal error while executing the job : " + scheduleExecution.getJob().getId(), e);
            } finally {
                scheduleExecution = executionManagementService.finishExecution(scheduleExecution.getId(), status);
            }
        } catch (AbstractServiceException e) {
            throw new SystemIntegrityException("Fail to finish execution", e);
        }

        return scheduleExecution;
    }

    protected ScheduleExecutionStatus executeJobNodes(ScheduleExecution scheduleExecution)
        throws AbstractServiceException
    {
        // Check if node list not empty
        if (CollectionUtils.isEmpty(scheduleExecution.getNodes())) {
            return ScheduleExecutionStatus.EMPTYNODES;
        }

        // Remember when the process started
        ChronometerTimer timer = new ChronometerTimer(chronometer);

        // Succeed nodes counter
        int succeedNodes = 0;

        // All nodes iterator
        Iterator<ScheduleExecutionNode> nodeIterator = scheduleExecution.getNodes().iterator();

        // Start loop throught all execution nodes
        while (nodeIterator.hasNext()) {
            // Get fresh copy of execution and check the cancellation flag
            ScheduleExecution scheduleExecutionReloaded =
                executionManagementService.findExecution(scheduleExecution.getId());
            if (scheduleExecutionReloaded.isCancelled()) {
                LOGGER.debug("Execution [{}] is cancelled", scheduleExecution.getId());
                return ScheduleExecutionStatus.CANCELED;
            }

            // Current node
            ScheduleExecutionNode currentNode = nodeIterator.next();

            // Execution action on node
            ScheduleExecutionResult scheduleExecutionResult = executeJobNode(scheduleExecution, currentNode);

            // If last action succeedes break the node loop
            if (scheduleExecutionResult.isSucceed()) {
                LOGGER.debug("Success execution [{}] on node [{}]",
                    scheduleExecution.getId(), currentNode.getAddress());
                succeedNodes++;
                if (!scheduleExecution.isAllNodes()) {
                    return ScheduleExecutionStatus.SUCCEED;
                }
            } else {
                LOGGER.debug("Failed execution [{}] on node [{}]",
                    scheduleExecution.getId(), currentNode.getAddress());
            }

            // Are there any other pending nodes?
            if (nodeIterator.hasNext()) {
                // Check timeout
                if (scheduleExecution.getTimeout() > 0) {
                    if (timer.elapsed() > scheduleExecution.getTimeout()) {
                        LOGGER.debug("Execution [{}] is timed out", scheduleExecution.getId());
                        return ScheduleExecutionStatus.TIMEOUT;
                    }
                }
            } else {
                if (scheduleExecution.isAllNodes()) {
                    if (succeedNodes == scheduleExecution.getNodes().size()) {
                        return ScheduleExecutionStatus.SUCCEED;
                    }
                }
            }
        }

        return ScheduleExecutionStatus.FAILED;
    }

    protected ScheduleExecutionResult executeJobNode(ScheduleExecution scheduleExecution, ScheduleExecutionNode node)
        throws AbstractServiceException
    {
        LOGGER.debug("Start execution [{}] on node [{}]", scheduleExecution.getId(), node.getAddress());

        // Register node execution start, execute and register finish
        ScheduleExecutionResult scheduleExecutionResult =
            executionManagementService.startExecutionResult(node.getId());

        ScheduleAction scheduleAction = scheduleExecution.getAction();

        // Execute action
        boolean succeed = false;
        byte[] data = null;

        try {
            ActionAgentResult actionAgentResult = actionAgent.executeAction(scheduleExecution.getId(),
                scheduleAction.getIdentifier(), scheduleAction.getDefinition(), node.getAddress());
            succeed = actionAgentResult.isSucceed();
            data = actionAgentResult.getData();
        } finally {
            scheduleExecutionResult = executionManagementService.finishExecutionResult(
                scheduleExecutionResult.getId(), succeed, data);
        }

        return scheduleExecutionResult;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }

    @Required
    public void setExecutionManagementService(ScheduleExecutionManagementService executionManagementService) {
        this.executionManagementService = executionManagementService;
    }

    @Required
    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    @Required
    public void setActionAgent(ActionAgent actionAgent) {
        this.actionAgent = actionAgent;
    }

    @Required
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}
