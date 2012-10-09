package org.qzerver.model.agent.action.providers.marshaller.impl;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.agent.action.providers.marshaller.ActionDefinitionMarshallerException;

public class ActionDefinitionJsonMarshaller implements ActionDefinitionMarshaller {

    @Override
    public byte[] marshall(ActionDefinition actionDefinition) {
        return null;
    }

    @Override
    public ActionDefinition unmarshall(ActionIdentifier actionIdentifier, byte[] definition)
        throws ActionDefinitionMarshallerException
    {
        return null;
    }

}
