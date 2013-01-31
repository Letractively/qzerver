package org.qzerver.model.agent.action.providers.executor.groovy;

import org.hibernate.validator.constraints.NotBlank;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionIdentifier;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

public class GroovyActionDefinition implements ActionDefinition, Serializable {

    @NotNull
    @NotBlank
    private String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public ActionIdentifier getIdentifier() {
        return ActionIdentifier.GROOVY;
    }
}
