package org.qzerver.model.service.quartz.executor.impl;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
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


@Transactional(propagation = Propagation.NEVER)
public class QuartzExecutorServiceImpl implements QuartzExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzExecutorServiceImpl.class);

    private Validator beanValidator;

    private ScheduleExecutionManagementService executionManagementService;

    @Override
    public void executeJob(QuartzExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] is about to execute", parameters.getScheduleJobId());

        // Compose execution
        StartJobExecutionParameters executionParameters = new StartJobExecutionParameters();
        executionParameters.setScheduleJobId(parameters.getScheduleJobId());
        executionParameters.setScheduled(parameters.getScheduled());
        executionParameters.setFired(parameters.getFired());

        ScheduleExecution scheduleExecution = executionManagementService.startExecution(executionParameters);

        // Pessimistic about execution
        boolean succeed = false;

        try {
            // Start loop throught all execution nodes
            for (ScheduleExecutionNode node : scheduleExecution.getNodes()) {
                // Register execution result
                ScheduleExecutionResult scheduleExecutionResult = executionManagementService.startExecutionResult(node.getId());

                ActionResult actionResult = null;

                try {
                    actionResult = null; // execute action
                } finally {
                    // Finish execution result report
                    executionManagementService.finishExecutionResult(scheduleExecutionResult.getId(), actionResult);
                }

                // If last action succeedes break the node loop
                if ((actionResult != null) && actionResult.isSucceed()) {
                    succeed = true;
                    break;
                }

                // Get fresh copy of execution and check cancel flag
                ScheduleExecution scheduleExecutionReloaded = executionManagementService.getExecution(scheduleExecution.getId());
                if (scheduleExecutionReloaded.isCancelled()) {
                    break;
                }
            }
        } finally {
            // Finish execution
            executionManagementService.finishExecution(scheduleExecution.getId(), succeed);
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
}
