package org.qzerver.model.service.job.executor.impl;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.qzerver.model.service.job.executor.ScheduleJobExecutorService;
import org.qzerver.model.service.job.executor.dto.AutomaticJobExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.Iterator;

@Transactional(propagation = Propagation.NEVER)
public class ScheduleJobExecutorServiceImpl implements ScheduleJobExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobExecutorServiceImpl.class);

    private Validator beanValidator;

    private ScheduleExecutionManagementService executionManagementService;

    private Chronometer chronometer;

    private ActionAgent actionAgent;

    @Override
    public ScheduleExecution executeAutomaticJob(AutomaticJobExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] will be executed (auto)", parameters.getScheduleJobId());

        StartExecutionParameters executionParameters = new StartExecutionParameters();
        executionParameters.setScheduleJobId(parameters.getScheduleJobId());
        executionParameters.setScheduled(parameters.getScheduled());
        executionParameters.setFired(parameters.getFired());
        executionParameters.setManual(false);

        return executeJob(executionParameters);
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

    @SuppressWarnings({"ConstantConditions"})
    protected ScheduleExecution executeJob(StartExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        // Compose execution descriptor
        ScheduleExecution scheduleExecution = executionManagementService.startExecution(parameters);

         // Pessimistic about execution
        ScheduleExecutionStatus status = ScheduleExecutionStatus.FAILED;
        int succeedNodes = 0;

        try {
            // Nodes iterator
            Iterator<ScheduleExecutionNode> nodeIterator = scheduleExecution.getNodes().iterator();

            // Start loop throught all execution nodes
            while (nodeIterator.hasNext()) {
                // Get fresh copy of execution and check the cancellation flag
                ScheduleExecution scheduleExecutionLoaded = executionManagementService.getExecution(scheduleExecution.getId());
                if (scheduleExecutionLoaded.isCancelled()) {
                    status = ScheduleExecutionStatus.CANCELED;
                    break;
                }

                // Current node
                ScheduleExecutionNode node = nodeIterator.next();

                LOGGER.debug("Start execution [{}] on node [{}]", scheduleExecution.getName(), node.getAddress());

                // Register node execution start, execute and register finish
                ScheduleExecutionResult scheduleExecutionResult = executionManagementService.startExecutionResult(node.getId());

                ActionResult actionResult = null;
                try {
                    actionResult = actionAgent.executeAction(scheduleExecution.getAction(), node);
                } finally {
                    scheduleExecutionResult = executionManagementService.finishExecutionResult(scheduleExecutionResult.getId(), actionResult);
                }

                // If last action succeedes break the node loop
                if (actionResult != null && actionResult.isSucceed()) {
                    LOGGER.debug("Success execution [{}] on node [{}]", scheduleExecution.getName(), node.getAddress());
                    succeedNodes++;
                    if (! scheduleExecution.isAllNodes()) {
                        status = ScheduleExecutionStatus.SUCCEED;
                        break;
                    }
                } else {
                    LOGGER.debug("Failed execution [{}] on node [{}]", scheduleExecution.getName(), node.getAddress());
                }

                // Are there any other pending nodes?
                if (nodeIterator.hasNext()) {
                    // Check timeout
                    if (scheduleExecution.getTimeout() > 0) {
                        long durationMs = scheduleExecutionResult.getFinished().getTime() - scheduleExecution.getStarted().getTime();
                        if (durationMs > scheduleExecution.getTimeout()) {
                            LOGGER.debug("Time is out for execution [{}]", scheduleExecution.getName());
                            status = ScheduleExecutionStatus.TIMEOUT;
                            break;
                        }
                    }
                } else {
                    if (scheduleExecution.isAllNodes()) {
                        if (succeedNodes == scheduleExecution.getNodes().size()) {
                            status = ScheduleExecutionStatus.SUCCEED;
                        }
                    }
                }
            }
        } catch (Exception e) {
            status = ScheduleExecutionStatus.EXCEPTION;
            LOGGER.error("Internal error while executing the job : " + scheduleExecution.getJob().getId(), e);
        } finally {
            // Finish execution report
            scheduleExecution = executionManagementService.finishExecution(scheduleExecution.getId(), status);
        }

        return scheduleExecution;
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
}
