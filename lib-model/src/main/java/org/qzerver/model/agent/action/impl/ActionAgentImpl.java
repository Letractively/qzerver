package org.qzerver.model.agent.action.impl;

import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.agent.action.ActionAgentResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class ActionAgentImpl implements ActionAgent {

    @Override
    public ActionAgentResult executeAction(long scheduleExecutionId,
        String actionType, byte[] actionDefinition, String address)
    {
        ActionAgentResult actionAgentResult = new ActionAgentResult();
        actionAgentResult.setSucceed(false);
        actionAgentResult.setData("<xml></xml>".getBytes());

        return actionAgentResult;
    }

}
