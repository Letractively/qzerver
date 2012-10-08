package org.qzerver.model.domain.action.marshaller;

import org.qzerver.model.domain.action.ActionResult;

/**
 * Marshaller for action result
 */
public interface ActionResultMarshaller {

    byte[] marshallResult(ActionResult actionResult);

    ActionResult unmarshallResult(String type, byte[] result) throws ActionResultMarshallerException;

}
