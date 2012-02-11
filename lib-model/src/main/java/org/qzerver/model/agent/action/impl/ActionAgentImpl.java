package org.qzerver.model.agent.action.impl;

import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleAction;
import org.qzerver.model.domain.entities.job.ScheduleExecutionNode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class ActionAgentImpl implements ActionAgent {

    @Override
    public ActionResult executeAction(ScheduleAction action, ScheduleExecutionNode node) {
        return null;
    }
}
