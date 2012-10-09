package org.qzerver.model.agent.action.providers.marshaller;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

/**
 * Marshaller for action definition
 */
public interface ActionDefinitionMarshaller {

    /**
     * Marshall action definition
     * @param actionDefinition Action definition object
     * @return Serialized data
     */
    byte[] marshall(ActionDefinition actionDefinition);

    /**
     * Unmarshall action definition
     * @param actionIdentifier Action identifier
     * @param definition Seriailized data
     * @return Action definition object
     * @throws ActionDefinitionMarshallerException Exception on error
     */
    ActionDefinition unmarshall(ActionIdentifier actionIdentifier, byte[] definition)
        throws ActionDefinitionMarshallerException;

}
