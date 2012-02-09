package org.qzerver.model.service.job;

import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.domain.entities.job.ScheduleExecutionResult;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface ScheduleExecutionManagementService {

    ScheduleExecution startExecution(long scheduleJobId, Date scheduled, Date fired);

    ScheduleExecutionResult startExecutionResult(long scheduleExecutionNodeId);

    ScheduleExecutionResult finishExecutionResult(long scheduleExecutionResultId, ActionResult result);

    ScheduleExecution finishExecution(long scheduleExecutionId, boolean succeed);

    ScheduleExecution getExecution(long scheduleExecutionId);

    void cancelExecution(long scheduleExecutionId);

}
