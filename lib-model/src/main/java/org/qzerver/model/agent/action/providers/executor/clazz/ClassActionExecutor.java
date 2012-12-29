package org.qzerver.model.agent.action.providers.executor.clazz;

import com.google.common.base.Preconditions;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.ActionResult;
import org.springframework.beans.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ClassActionExecutor implements ActionExecutor {

    @Override
    public ActionResult execute(ActionDefinition actionDefinition, long scheduleExecutionId, String nodeAddress) {
        Preconditions.checkNotNull(actionDefinition, "Action definition is not set");
        Preconditions.checkNotNull(nodeAddress, "Node address is not set");

        ClassActionDefinition classActionDefinition = (ClassActionDefinition) actionDefinition;

        Object callable;

        if (classActionDefinition.getCallableInstance() == null) {
            // Search for class
            Class<?> classToExecute;

            try {
                classToExecute = Class.forName(classActionDefinition.getCallableClassName());
            } catch (Exception e) {
                return new ClassActionResult(e);
            }

            // Check is it an implementation of java.util.concurrent.Callable
            if (!Callable.class.isAssignableFrom(classToExecute)) {
                return new ClassActionResult(IllegalArgumentException.class.getCanonicalName(),
                    "Class must implement java.util.concurrent.Callable interface", null);
            }

            // Instantiate class object
            try {
                callable = BeanUtils.instantiate(classToExecute);
            } catch (Exception e) {
                return new ClassActionResult(e);
            }
        } else {
            callable = classActionDefinition.getCallableInstance();
        }

        // Set object properties
        Map<String, String> properties = new HashMap<String, String>(classActionDefinition.getParameters());
        properties.put("nodeAddress", nodeAddress);

        return executeCallable((Callable<?>) callable, properties);
    }

    private ActionResult executeCallable(Callable<?> callable, Map<String, String> properties) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(callable);
        PropertyValues propertyValues = new MutablePropertyValues(properties);

        try {
            beanWrapper.setPropertyValues(propertyValues, true, false);
        } catch (Exception e) {
            return new ClassActionResult(e);
        }

        return executeCallableInitialized(callable);
    }

    private ActionResult executeCallableInitialized(Callable<?> callable) {
        Object result;

        try {
            result = callable.call();
        } catch (Exception e) {
            return new ClassActionResult(e);
        }

        String resultAsText = String.valueOf(result);
        return new ClassActionResult(resultAsText);
    }

}
