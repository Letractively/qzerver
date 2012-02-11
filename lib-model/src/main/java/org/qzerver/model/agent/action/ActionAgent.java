package org.qzerver.model.agent.action;

import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.springframework.stereotype.Service;

@Service
public interface ActionAgent {

    ActionResult executeAction(ScheduleAction action, ScheduleExecutionNode node);


}
