package org.qzerver.model.domain.action.marshaller;

import org.qzerver.model.domain.action.ActionDefinition;

/**
 * Marshaller for action definition
 */
public interface ActionDefinitionMarshaller {

    byte[] marshall(ActionDefinition actionDefinition);

    ActionDefinition unmarshall(String type, byte[] definition) throws ActionDefinitionMarshallerException;

}
