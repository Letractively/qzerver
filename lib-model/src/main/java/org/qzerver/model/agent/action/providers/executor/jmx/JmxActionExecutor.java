package org.qzerver.model.agent.action.providers.executor.jmx;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.qzerver.model.agent.action.providers.ActionDefinition;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmxActionExecutor implements ActionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxActionExecutor.class);

    private static final String JMX_PARAM_NODE = "\\$\\{nodeAddress\\}";

    private static final String JMX_PARAM_EXECUTION = "\\$\\{executionId\\}";

    private Validator beanValidator;

    @Override
    public JmxActionResult execute(ActionDefinition actionDefinition,
        long scheduleExecutionId, String nodeAddress)
    {
        Preconditions.checkNotNull(actionDefinition, "Definition is null");
        Preconditions.checkNotNull(nodeAddress, "Node is not specified");

        BeanValidationUtils.checkValidity(actionDefinition, beanValidator);

        JmxActionDefinition definition = (JmxActionDefinition) actionDefinition;

        LOGGER.debug("Execute jmx call action on node [{}]", nodeAddress);

        String effectiveUrl = definition.getUrl();
        effectiveUrl = effectiveUrl.replaceAll(JMX_PARAM_NODE, nodeAddress);

        try {
            JMXServiceURL jmxUrl = new JMXServiceURL(effectiveUrl);

            return processJmxUrl(jmxUrl, definition);
        } catch (Exception e) {
            LOGGER.warn("Fail to execute jmx call", e);
            return produceExceptionalResult(e);
        }
    }

    private JmxActionResult produceExceptionalResult(Exception e) {
        JmxActionResult result = new JmxActionResult();

        result.setExceptionClass(e.getClass().getCanonicalName());
        result.setExceptionMessage(e.getLocalizedMessage());
        result.setStatus(JmxActionResultStatus.EXCEPTION);
        result.setResult(null);

        return result;
    }

    private JmxActionResult processJmxUrl(JMXServiceURL jmxUrl, JmxActionDefinition definition)
        throws Exception
    {
        Map<String, Object> environment = new HashMap<String, Object>();

        if (definition.getUsername() != null) {
            String[] credentials = new String[] {
                definition.getUsername(),
                definition.getPassword()
            };
            environment.put(JMXConnector.CREDENTIALS, credentials);
        }

        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxUrl, environment);

        try {
            return processJmxConnector(jmxConnector, definition);
        } finally {
            jmxConnector.close();
        }
    }

    private JmxActionResult processJmxConnector(JMXConnector jmxConnector, JmxActionDefinition definition)
        throws Exception
    {
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

        // Compose parameters
        List<String> parameterList = definition.getParameters();
        String[] parameterArray = null;
        String[] signatureArray = null;

        if (CollectionUtils.isNotEmpty(parameterList)) {
            int count = parameterList.size();
            parameterArray = new String[count];
            signatureArray = new String[count];
            for (int i = 0; i < count; i++) {
                parameterArray[i] = parameterList.get(i);
                signatureArray[i] = "java.lang.String";
            }
        }

        // Make call
        ObjectName objectName = new ObjectName(definition.getBean());
        Object resultObject = mBeanServerConnection.invoke(objectName, definition.getMethod(),
            parameterArray, signatureArray);

        // Convert object to text
        String resultText = String.valueOf(resultObject);

        // Compose result
        JmxActionResult result = new JmxActionResult();
        result.setResult(resultText);
        result.setStatus(JmxActionResultStatus.CALLED);

        return result;
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
