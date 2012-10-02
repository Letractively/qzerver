package org.qzerver.model.domain.action.marshaller;

import org.qzerver.model.domain.action.ActionDefinition;
import org.qzerver.model.domain.action.ActionResult;
/**
 * Marshaller for action result and action definition
 */
public interface ActionMarshaller {

    byte[] marshallDefinition(ActionDefinition actionDefinition);

    ActionDefinition unmarshallDefinition(String type, byte[] definition) throws ActionMarshallerException;

    byte[] marshallResult(ActionResult actionResult);

    ActionResult unmarshallResult(String type, byte[] result) throws ActionMarshallerException;

}
