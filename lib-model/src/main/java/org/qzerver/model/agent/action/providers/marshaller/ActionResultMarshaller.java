package org.qzerver.model.agent.action.providers.marshaller;

import org.qzerver.model.agent.action.providers.ActionIdentifier;
import org.qzerver.model.agent.action.providers.ActionResult;

/**
 * Marshaller for action result
 */
public interface ActionResultMarshaller {

    /**
     * Marshall action result
     * @param actionResult Action result object
     * @return Serialized data
     */
    byte[] marshallResult(ActionResult actionResult);

    /**
     * Unmarshall action result
     * @param actionIdentifier Action identifier
     * @param result Serialized data
     * @return Action result object
     * @throws ActionResultMarshallerException Exception on error
     */
    ActionResult unmarshallResult(ActionIdentifier actionIdentifier, byte[] result)
        throws ActionResultMarshallerException;

}
