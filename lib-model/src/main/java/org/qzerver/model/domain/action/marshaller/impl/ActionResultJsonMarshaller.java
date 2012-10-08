package org.qzerver.model.domain.action.marshaller.impl;

import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.action.marshaller.ActionResultMarshallerException;
import org.qzerver.model.domain.action.marshaller.ActionResultMarshaller;

public class ActionResultJsonMarshaller implements ActionResultMarshaller {

    @Override
    public byte[] marshallResult(ActionResult actionDefinition) {
        return null;
    }

    @Override
    public ActionResult unmarshallResult(String type, byte[] result) throws ActionResultMarshallerException {
        return null;
    }

}
