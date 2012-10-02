package org.qzerver.model.domain.action.marshaller;


public class ActionMarshallerException extends Exception {

    public ActionMarshallerException() {
    }

    public ActionMarshallerException(Throwable cause) {
        super(cause);
    }

    public ActionMarshallerException(String message) {
        super(message);
    }

    public ActionMarshallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
