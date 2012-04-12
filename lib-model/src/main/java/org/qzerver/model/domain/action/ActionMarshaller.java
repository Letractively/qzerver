package org.qzerver.model.domain.action;

/**
 * Action marshaller
 * @param <T> Action type
 */
public interface ActionMarshaller<T> {

    String marshall(T bean);

    T unmarshall(String text);

}
