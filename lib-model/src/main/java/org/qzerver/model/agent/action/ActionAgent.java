package org.qzerver.model.agent.action;

import org.springframework.stereotype.Service;

/**
 * Action executor
 */
@Service
public interface ActionAgent {

    /**
     * Executes an action with specified parameters and returns result
     * @param scheduleExecutionId Schedule execution identifier (can be used as an unique parameter for each query)
     * @param identifier Action type
     * @param definition Definition of the action
     * @param address Address where to execute
     * @return Result of the action
     */
    ActionAgentResult executeAction(long scheduleExecutionId,
        String identifier, byte[] definition, String address);

}
