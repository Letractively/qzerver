package org.qzerver.model.domain.action;

public interface ActionMarshaller<T> {

    String marshall(T bean);

    T unmarshall(String text);

}
