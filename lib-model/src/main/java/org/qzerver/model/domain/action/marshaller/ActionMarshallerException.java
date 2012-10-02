package org.qzerver.model.domain.action.marshaller;

import org.qzerver.model.domain.entities.job.ScheduleActionType;

public class ActionMarshallerException extends Exception {

    private final ScheduleActionType type;

    public ActionMarshallerException(ScheduleActionType type) {
        this.type = type;
    }

    public ActionMarshallerException(ScheduleActionType type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public ActionMarshallerException(ScheduleActionType type, String message) {
        super(message);
        this.type = type;
    }

    public ActionMarshallerException(ScheduleActionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
}
