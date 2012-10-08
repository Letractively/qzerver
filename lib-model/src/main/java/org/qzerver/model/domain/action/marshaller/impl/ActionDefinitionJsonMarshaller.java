package org.qzerver.model.domain.action.marshaller.impl;

import org.qzerver.model.domain.action.ActionDefinition;
import org.qzerver.model.domain.action.marshaller.ActionDefinitionMarshaller;
import org.qzerver.model.domain.action.marshaller.ActionDefinitionMarshallerException;

public class ActionDefinitionJsonMarshaller implements ActionDefinitionMarshaller {

    @Override
    public byte[] marshall(ActionDefinition actionDefinition) {
        return null;
    }

    @Override
    public ActionDefinition unmarshall(String type, byte[] definition) throws ActionDefinitionMarshallerException {
        return null;
    }

}
