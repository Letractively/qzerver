package org.qzerver.model.domain.action;

/**
 * Definition of the action
 */
public interface ActionDefinition {

    /**
     * Return the unique string which describes this type of action
     * @return Unique type identifier
     */
    String getType();

}
