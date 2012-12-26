package org.qzerver.model.agent.action.providers.executor.clazz;

import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import java.util.Map;

public class ClazzActionDefinition implements ActionDefinition {

    private String className;

    private Map<String, String> parameters;

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.CLASS;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

}
