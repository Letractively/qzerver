package org.qzerver.model.agent.action.providers;

import org.qzerver.model.agent.action.providers.executor.http.HttpActionDefinition;
import org.qzerver.model.agent.action.providers.executor.http.HttpActionResult;
import org.qzerver.model.agent.action.providers.executor.jmx.JmxActionDefinition;
import org.qzerver.model.agent.action.providers.executor.jmx.JmxActionResult;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionDefinition;
import org.qzerver.model.agent.action.providers.executor.localcommand.LocalCommandActionResult;
import org.qzerver.model.agent.action.providers.executor.socket.SocketActionDefinition;
import org.qzerver.model.agent.action.providers.executor.socket.SocketActionResult;
import org.qzerver.model.agent.action.providers.executor.sshcommand.SshCommandActionDefinition;
import org.qzerver.model.agent.action.providers.executor.sshcommand.SshCommandActionResult;

public enum ActionIdentifier {

    LOCAL_COMMAND("action.local.command",
        LocalCommandActionDefinition.class,
        LocalCommandActionResult.class
    ),

    SSH_COMMAND("action.ssh.command",
        SshCommandActionDefinition.class,
        SshCommandActionResult.class
    ),

    HTTP("action.http",
        HttpActionDefinition.class,
        HttpActionResult.class
    ),

    SOCKET("action.socket",
        SocketActionDefinition.class,
        SocketActionResult.class
    ),

    JMX("action.jmx",
        JmxActionDefinition.class,
        JmxActionResult.class
    );

    private final String identifier;

    private final Class<? extends ActionDefinition> actionDefinitionClass;

    private final Class<? extends ActionResult> actionResultClass;

    private ActionIdentifier(String identifier,
        Class<? extends ActionDefinition> actionDefinitionClass,
        Class<? extends ActionResult> actionResultClass)
    {
        this.identifier = identifier;
        this.actionDefinitionClass = actionDefinitionClass;
        this.actionResultClass = actionResultClass;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Class<? extends ActionDefinition> getActionDefinitionClass() {
        return actionDefinitionClass;
    }

    public Class<? extends ActionResult> getActionResultClass() {
        return actionResultClass;
    }

    public static ActionIdentifier findByIdentifier(String identifier) {
        for (ActionIdentifier actionIdentifier : ActionIdentifier.values()) {
            if (actionIdentifier.getIdentifier().equals(identifier)) {
                return actionIdentifier;
            }
        }

        throw new IllegalArgumentException("Can't fing action identifier by specified itentifier: " + identifier);
    }

}
