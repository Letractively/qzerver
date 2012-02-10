package org.qzerver.model.service.quartz.executor.impl;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.gainmatrix.lib.time.Chronometer;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.service.job.execution.ScheduleExecutionManagementService;
import org.qzerver.model.service.job.execution.dto.StartJobExecutionParameters;
import org.qzerver.model.service.quartz.executor.QuartzExecutorService;
import org.qzerver.model.service.quartz.executor.dto.QuartzExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.Iterator;

@Transactional(propagation = Propagation.NEVER)
public class QuartzExecutorServiceImpl implements QuartzExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzExecutorServiceImpl.class);

    private Validator beanValidator;

    private ScheduleExecutionManagementService executionManagementService;

    private Chronometer chronometer;

    @Override
    public void executeAutomaticJob(QuartzExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] will be executed (auto)", parameters.getScheduleJobId());

        StartJobExecutionParameters executionParameters = new StartJobExecutionParameters();
        executionParameters.setScheduleJobId(parameters.getScheduleJobId());
        executionParameters.setScheduled(parameters.getScheduled());
        executionParameters.setFired(parameters.getFired());
        executionParameters.setManual(false);

        executeJob(executionParameters);
    }

    @Override
    public void executeManualJob(long scheduleJobId) {
        LOGGER.debug("Job [id={}] will be executed (manual)", scheduleJobId);

        Date now = chronometer.getCurrentMoment();

        StartJobExecutionParameters executionParameters = new StartJobExecutionParameters();
        executionParameters.setScheduleJobId(scheduleJobId);
        executionParameters.setScheduled(now);
        executionParameters.setFired(now);
        executionParameters.setManual(true);

        executeJob(executionParameters);
    }

    protected void executeJob(StartJobExecutionParameters executionParameters) {
        // Compose execution descriptor
        ScheduleExecution scheduleExecution = executionManagementService.startExecution(executionParameters);

         // Pessimistic about execution
        ScheduleExecutionStatus status = ScheduleExecutionStatus.EXCEPTION;

        try {
            // Nodes iterator
            Iterator<ScheduleExecutionNode> nodeIterator = scheduleExecution.getNodes().iterator();

            // Start loop throught all execution nodes
            while (nodeIterator.hasNext()) {
                // Current node
                ScheduleExecutionNode node = nodeIterator.next();

                // Register execution result
                ScheduleExecutionResult scheduleExecutionResult = executionManagementService.startExecutionResult(node.getId());

                ActionResult actionResult = null;

                try {
                    actionResult = null; // execute action
                } finally {
                    // Finish execution result report
                    scheduleExecutionResult = executionManagementService.finishExecutionResult(scheduleExecutionResult.getId(), actionResult);
                }

                // If last action succeedes break the node loop
                if ((actionResult != null) && actionResult.isSucceed()) {
                    status = ScheduleExecutionStatus.SUCCESS;
                    break;
                }

                // Are there any other pending nodes?
                if (nodeIterator.hasNext()) {
                    // Get fresh copy of execution and check cancel flag
                    ScheduleExecution scheduleExecutionReloaded = executionManagementService.getExecution(scheduleExecution.getId());
                    if (scheduleExecutionReloaded.isCancelled()) {
                        status = ScheduleExecutionStatus.CANCELED;
                        break;
                    }

                    // Check timeout
                    if (scheduleExecution.getTimeoutMs() > 0) {
                        long durationMs = scheduleExecutionResult.getFinished().getTime() - scheduleExecution.getFired().getTime();
                        if (durationMs > scheduleExecution.getTimeoutMs()) {
                            status = ScheduleExecutionStatus.LIMIT_DURATION;
                            break;
                        }
                    }
                } else {
                    if (scheduleExecution.getNodesTotalNumber() > scheduleExecution.getNodes().size()) {
                        status = ScheduleExecutionStatus.LIMIT_TRIALS;
                    } else {
                        status = ScheduleExecutionStatus.ALL_FAILED;
                    }
                }
            }
        } finally {
            // Finish execution report
            executionManagementService.finishExecution(scheduleExecution.getId(), status);
        }
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
}
