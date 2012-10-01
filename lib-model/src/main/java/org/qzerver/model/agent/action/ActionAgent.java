package org.qzerver.model.agent.action;

import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.springframework.stereotype.Service;

/**
 * Action executor
 */
@Service
public interface ActionAgent {

    /**
     * Executes an action with specified parameters and returns result. Method should catch any exception occured and
     * return a correct result object
     * @param scheduleExecutionId Schedule execution identifier (can be used as an unique parameter for each query)
     * @param action Action
     * @param address Address to execute on
     * @return Result of the action
     */
    ActionResult executeAction(long scheduleExecutionId, ScheduleAction action, String address);


}
