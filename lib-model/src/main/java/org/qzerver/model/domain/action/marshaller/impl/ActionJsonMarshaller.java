package org.qzerver.model.domain.action.marshaller.impl;

import org.qzerver.model.domain.action.ActionDefinition;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.action.marshaller.ActionMarshaller;
import org.qzerver.model.domain.action.marshaller.ActionMarshallerException;

public class ActionJsonMarshaller implements ActionMarshaller {

    @Override
    public byte[] marshallDefinition(ActionDefinition actionDefinition) {
        return null;
    }

    @Override
    public byte[] marshallResult(ActionResult actionDefinition) {
        return null;
    }

    @Override
    public ActionDefinition unmarshallDefinition(String type, byte[] definition)
        throws ActionMarshallerException
    {
        return null;
    }

    @Override
    public ActionResult unmarshallResult(String type, byte[] result)
        throws ActionMarshallerException
    {
        return null;
    }

}
