package org.qzerver.model.service.job.execution;

import com.gainmatrix.lib.paging.Extraction;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecutionStatus;
import org.qzerver.model.service.job.execution.dto.StartExecutionParameters;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Schedule execution internal functions
 */
@Service
public interface ScheduleExecutionManagementService {

    /**
     * Register start of execution
     * @param parameters Execution parameters
     * @return Schedule job entity
     */
    ScheduleExecution startExecution(StartExecutionParameters parameters);

    /**
     * Register start of node execution
     * @param scheduleExecutionNodeId Schedule execution node identifier
     * @return Schedule execution entity
     */
    ScheduleExecutionResult startExecutionResult(long scheduleExecutionNodeId);

    /**
     * Register finish of node execution
     * @param scheduleExecutionResultId Schedule execution result identifier
     * @param actionResult Payload of execution
     * @return Schedule execution entity
     */
    ScheduleExecutionResult finishExecutionResult(long scheduleExecutionResultId, ActionResult actionResult);

    /**
     * Register finish of the whole execution
     * @param scheduleExecutionId Schedule execution identifier
     * @param status Status of execution
     * @return Schedule execution entity
     */
    ScheduleExecution finishExecution(long scheduleExecutionId, ScheduleExecutionStatus status);

    /**
     * Request execution entity
     * @param scheduleExecutionId Schedule execution identifier
     * @return Schedule execution entity
     */
    ScheduleExecution getExecution(long scheduleExecutionId);

    /**
     * Set cancellation flag
     * @param scheduleExecutionId Schedule execution identifier
     */
    void cancelExecution(long scheduleExecutionId);

    /**
     * Get all last executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> getAll(Extraction extraction);

    /**
     * Get all last closed executions
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> getFinished(Extraction extraction);

    /**
     * Get all last executions in progress
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> getEngaged(Extraction extraction);

    /**
     * Get last executions by job
     * @param scheduleJobId Schedule job identifier
     * @param extraction Extraction
     * @return Execution list
     */
    List<ScheduleExecution> getByJob(long scheduleJobId, Extraction extraction);

}
