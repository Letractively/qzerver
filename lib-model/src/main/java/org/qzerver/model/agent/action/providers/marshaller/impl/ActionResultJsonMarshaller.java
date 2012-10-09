package org.qzerver.model.agent.action.providers.marshaller.impl;

import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshallerException;
import org.qzerver.model.agent.action.providers.marshaller.ActionResultMarshaller;

public class ActionResultJsonMarshaller implements ActionResultMarshaller {

    @Override
    public byte[] marshallResult(ActionResult actionDefinition) {
        return null;
    }

    @Override
    public ActionResult unmarshallResult(ActionIdentifier actionIdentifier, byte[] result)
        throws ActionResultMarshallerException
    {
        return null;
    }

}
