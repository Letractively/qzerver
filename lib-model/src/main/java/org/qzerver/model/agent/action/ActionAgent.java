package org.qzerver.model.agent.action;

import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.springframework.stereotype.Service;

/**
 * Action executor
 */
@Service
public interface ActionAgent {

    /**
     * Executes an action with specified parameters and returns result
     * @param action Action
     * @param node Node to execute on
     * @return Result of the action
     */
    ActionResult executeAction(ScheduleAction action, ScheduleExecutionNode node);


}
