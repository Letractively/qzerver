package org.qzerver.model.agent.action;

import org.springframework.stereotype.Service;

/**
 * Action executor
 */
@Service
public interface ActionAgent {

    /**
     * Executes an action with specified parameters and returns result. Method should catch any exception occured and
     * return a correct result object
     * @param scheduleExecutionId Schedule execution identifier (can be used as an unique parameter for each query)
     * @param actionType Action type
     * @param actionDefinition Definition of the action
     * @param address Address where to execute
     * @return Result of the action
     */
    ActionAgentResult executeAction(long scheduleExecutionId,
        String actionType, byte[] actionDefinition, String address);

}
