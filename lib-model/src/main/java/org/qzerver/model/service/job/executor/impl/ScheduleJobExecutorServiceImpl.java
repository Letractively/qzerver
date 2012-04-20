package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
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
    public ScheduleExecution executeAutomaticJob(AutomaticJobExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] will be executed (auto)", parameters.getScheduleJobId());

        StartExecutionParameters executionParameters = new StartExecutionParameters();
        executionParameters.setScheduleJobId(parameters.getScheduleJobId());
        executionParameters.setScheduled(parameters.getScheduled());
        executionParameters.setFired(parameters.getFired());
        executionParameters.setManual(false);

        ScheduleExecution execution = executeJob(executionParameters);

        if (execution.getStatus() != ScheduleExecutionStatus.SUCCEED) {
            mailService.notifyJobExecutionFailed(execution);
        }

        return execution;
    }

    @Override
    public ScheduleExecution executeManualJob(long scheduleJobId) {
        LOGGER.debug("Job [id={}] will be executed (manual)", scheduleJobId);

        Date now = chronometer.getCurrentMoment();

        StartExecutionParameters executionParameters = new StartExecutionParameters();
        executionParameters.setScheduleJobId(scheduleJobId);
        executionParameters.setScheduled(now);
        executionParameters.setFired(now);
        executionParameters.setManual(true);

        return executeJob(executionParameters);
    }

    protected ScheduleExecution executeJob(StartExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        // Compose execution descriptor
        ScheduleExecution scheduleExecution = executionManagementService.startExecution(parameters);

        // The only reason the status is not assigned is that exception occurs in executeJobNodes() call
        ScheduleExecutionStatus status = ScheduleExecutionStatus.EXCEPTION;

        // Try to execute action on a node from the cluster
        try {
            status = executeJobNodes(scheduleExecution);
        } catch (Exception e) {
            LOGGER.error("Internal error while executing the job : " + scheduleExecution.getJob().getId(), e);
        } finally {
            // Finish execution report
            scheduleExecution = executionManagementService.finishExecution(scheduleExecution.getId(), status);
        }

        return scheduleExecution;
    }

    protected ScheduleExecutionStatus executeJobNodes(ScheduleExecution scheduleExecution) {
        // Succeed nodes counter
        int succeedNodes = 0;

        // All nodes iterator
        Iterator<ScheduleExecutionNode> nodeIterator = scheduleExecution.getNodes().iterator();

        // Start loop throught all execution nodes
        while (nodeIterator.hasNext()) {
            // Get fresh copy of execution and check the cancellation flag
            ScheduleExecution scheduleExecutionReloaded =
                executionManagementService.getExecution(scheduleExecution.getId());
            if (scheduleExecutionReloaded.isCancelled()) {
                LOGGER.debug("Execution [{}] is cancelled", scheduleExecution.getName());
                return ScheduleExecutionStatus.CANCELED;
            }

            // Current node
            ScheduleExecutionNode currentNode = nodeIterator.next();

            // Execution action on node
            ScheduleExecutionResult scheduleExecutionResult = executeJobNode(scheduleExecution, currentNode);

            // If last action succeedes break the node loop
            if (scheduleExecutionResult.isSucceed()) {
                LOGGER.debug("Success execution [{}] on node [{}]", scheduleExecution.getName(), currentNode.getAddress());
                succeedNodes++;
                if (!scheduleExecution.isAllNodes()) {
                    return ScheduleExecutionStatus.SUCCEED;
                }
            } else {
                LOGGER.debug("Failed execution [{}] on node [{}]", scheduleExecution.getName(), currentNode.getAddress());
            }

            // Are there any other pending nodes?
            if (nodeIterator.hasNext()) {
                // Check timeout
                if (scheduleExecution.getTimeout() > 0) {
                    long durationMs =
                        scheduleExecutionResult.getFinished().getTime() - scheduleExecution.getStarted().getTime();

                    if (durationMs > scheduleExecution.getTimeout()) {
                        LOGGER.debug("Execution [{}] is timed out", scheduleExecution.getName());
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

    protected ScheduleExecutionResult executeJobNode(ScheduleExecution scheduleExecution, ScheduleExecutionNode node) {
        LOGGER.debug("Start execution [{}] on node [{}]", scheduleExecution.getName(), node.getAddress());

        // Register node execution start, execute and register finish
        ScheduleExecutionResult scheduleExecutionResult =
            executionManagementService.startExecutionResult(node.getId());

        ActionResult actionResult = null;
        try {
            actionResult = actionAgent.executeAction(scheduleExecution.getAction(), node);
        } finally {
            scheduleExecutionResult =
                executionManagementService.finishExecutionResult(scheduleExecutionResult.getId(), actionResult);
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
