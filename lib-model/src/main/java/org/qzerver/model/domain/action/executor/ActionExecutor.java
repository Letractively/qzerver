package org.qzerver.model.domain.action.executor;

import org.qzerver.model.domain.action.ActionDefinition;
import org.qzerver.model.domain.action.ActionResult;

/**
 * Abstract action. Action should consume any checked exceptions and return correct result.
 * @param <R> Result type
 * @param <D> Definition type
 */
public interface ActionExecutor<R extends ActionResult, D extends ActionDefinition> {

    /**
     * Execute action
     * @param definition Definition of an action
     * @param nodeAddress Node address
     * @return Result of an action
     */
    R execute(D definition, String nodeAddress);

}
