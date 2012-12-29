package org.qzerver.model.agent.action.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.ActionAgent;
import org.qzerver.model.agent.action.ActionAgentResult;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshallerException;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshaller;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Transactional(propagation = Propagation.NEVER)
public class ActionAgentImpl implements ActionAgent {

    @NotNull
    private ActionResultMarshaller actionResultMarshaller;

    @NotNull
    private ActionDefinitionMarshaller actionDefinitionMarshaller;

    @NotNull
    private Map<ActionIdentifier, ActionExecutor> executors;

    @Override
    public ActionAgentResult executeAction(long scheduleExecutionId,
        String identifier, byte[] definition, String address)
    {
        Preconditions.checkNotNull(identifier, "Identifier is null");
        Preconditions.checkNotNull(definition, "Definition is null");
        Preconditions.checkNotNull(address, "Address is null");

        ActionIdentifier actionIdentifier = ActionIdentifier.findByIdentifier(identifier);

        ActionDefinition actionDefinition;

        try {
            actionDefinition = actionDefinitionMarshaller.unmarshall(actionIdentifier.getActionDefinitionClass(),
                definition);
        } catch (ActionDefinitionMarshallerException e) {
            throw new SystemIntegrityException("Fail to unmarshall definition", e);
        }

        ActionExecutor actionExecutor = executors.get(actionIdentifier);
        if (actionExecutor == null) {
            throw new NullPointerException("Executor is not found for identifier " + identifier);
        }

        ActionResult actionResult = actionExecutor.execute(actionDefinition, scheduleExecutionId, address);
        if (actionResult == null) {
            String message = String.format("Action result is null for execution=[%d] and node=[%s]",
                scheduleExecutionId, address);
            throw new NullPointerException(message);
        }

        byte[] actionResultData = actionResultMarshaller.marshall(actionResult);

        ActionAgentResult actionAgentResult = new ActionAgentResult();
        actionAgentResult.setSucceed(actionResult.isSucceed());
        actionAgentResult.setData(actionResultData);

        return actionAgentResult;
    }

    @Required
    public void setActionDefinitionMarshaller(ActionDefinitionMarshaller actionDefinitionMarshaller) {
        this.actionDefinitionMarshaller = actionDefinitionMarshaller;
    }

    @Required
    public void setActionResultMarshaller(ActionResultMarshaller actionResultMarshaller) {
        this.actionResultMarshaller = actionResultMarshaller;
    }

    @Required
    public void setExecutors(Map<ActionIdentifier, ActionExecutor> executors) {
        this.executors = executors;
    }

}
