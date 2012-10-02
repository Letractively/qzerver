package org.qzerver.model.domain.action.marshaller;

import org.qzerver.model.domain.action.ActionDefinition;
import org.qzerver.model.domain.action.ActionResult;
import org.qzerver.model.domain.entities.job.ScheduleActionType;

/**
 * Marshaller for action result and action definition
 */
public interface ActionMarshaller {

    byte[] marshallDefinition(ActionDefinition actionDefinition);

    ActionDefinition unmarshallDefinition(ScheduleActionType type, byte[] definition) throws ActionMarshallerException;

    byte[] marshallResult(ActionResult actionResult);

    ActionResult unmarshallResult(ScheduleActionType type, byte[] result) throws ActionMarshallerException;

}
