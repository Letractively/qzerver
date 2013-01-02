package org.qzerver.model.agent.action.providers.executor.jmx;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;
import org.qzerver.model.agent.action.providers.executor.jmx.beans.SampleJmxService;

import javax.annotation.Resource;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

public class JmxActionExecutorTest extends AbstractModelTest {

    private static final String MBEAN_NAME = "org.qzerver:type=SampleJmx";

    @Resource
    private ActionExecutor jmxActionExecutor;

    private JMXConnectorServer connectorServer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        LocateRegistry.createRegistry(3000);
    }

    @Before
    public void setUp() throws Exception {
        Map<String, Object> environment = new HashMap<String, Object>();
        environment.put("com.sun.management.jmxremote.ssl", "false");
        environment.put("com.sun.management.jmxremote.local.only", "false");
        environment.put(JMXConnectorServer.AUTHENTICATOR, new JmxAuthenticator("user", "pass"));

        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:3000/server");

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        SampleJmxService mbean = new SampleJmxService();

        ObjectName mbeanName = new ObjectName(MBEAN_NAME);
        mBeanServer.registerMBean(mbean, mbeanName);

        connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, environment, mBeanServer);
        connectorServer.start();

    }

    @After
    public void tearDown() throws Exception {
        connectorServer.stop();

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        ObjectName mbeanName = new ObjectName(MBEAN_NAME);
        mBeanServer.unregisterMBean(mbeanName);
    }

    @Test
    public void testNormal() throws Exception {
        JmxActionDefinition definition = new JmxActionDefinition();
        definition.setBean(MBEAN_NAME);
        definition.setMethod("method1");
        definition.setParameters(Lists.newArrayList("arg1", "arg2"));
        definition.setUsername("user");
        definition.setPassword("pass");
        definition.setUrl("service:jmx:rmi:///jndi/rmi://${nodeAddress}:3000/server");

        JmxActionResult result = (JmxActionResult) jmxActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isSucceed());
        Assert.assertEquals(JmxActionResultStatus.CALLED, result.getStatus());
        Assert.assertEquals("arg1arg2", result.getResult());
        Assert.assertNull(result.getExceptionClass());
        Assert.assertNull(result.getExceptionMessage());
    }

    @Test
    public void testException() throws Exception {
        JmxActionDefinition definition = new JmxActionDefinition();
        definition.setBean(MBEAN_NAME);
        definition.setMethod("method1");
        definition.setParameters(Lists.newArrayList("arg1", "arg2"));
        definition.setUsername("user");
        definition.setPassword("pass");
        definition.setUrl("service:jmx:rmi:///jndi/rmi://${nodeAddress}:3000/nonexisting");

        JmxActionResult result = (JmxActionResult) jmxActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(JmxActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertNull(result.getResult());
        Assert.assertEquals("java.io.IOException", result.getExceptionClass());
        Assert.assertTrue(StringUtils.contains(result.getExceptionMessage(), "javax.naming.NameNotFoundException: nonexisting"));
    }

    @Test
    public void testAuthentication() throws Exception {
        JmxActionDefinition definition = new JmxActionDefinition();
        definition.setBean(MBEAN_NAME);
        definition.setMethod("method1");
        definition.setParameters(Lists.newArrayList("arg1", "arg2"));
        definition.setUsername("user123");
        definition.setPassword("pass123");
        definition.setUrl("service:jmx:rmi:///jndi/rmi://${nodeAddress}:3000/server");

        JmxActionResult result = (JmxActionResult) jmxActionExecutor.execute(definition, 123L, "localhost");
        Assert.assertNotNull(result);
        Assert.assertEquals(false, result.isSucceed());
        Assert.assertEquals(JmxActionResultStatus.EXCEPTION, result.getStatus());
        Assert.assertNull(result.getResult());
        Assert.assertEquals("java.lang.SecurityException", result.getExceptionClass());
        Assert.assertEquals("Authentication failed", result.getExceptionMessage());
    }

    private static final class JmxAuthenticator implements JMXAuthenticator {

        private final String username;

        private final String password;

        private JmxAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public Subject authenticate(Object credentials) {
            if (credentials == null) {
                throw new SecurityException("Credentials are null");
            }

            if (! (credentials instanceof String[])) {
                throw new SecurityException("Wrong credentials class");
            }

            String[] authParameters = (String []) credentials;

            if (authParameters.length != 2) {
                throw new SecurityException("Credentials arrays must have just two elements");
            }

            String authLogin = authParameters[0];
            String authPassword = authParameters[1];

            if (username.equals(authLogin) && password.equals(authPassword)) {
                return new Subject();
            }

            throw new SecurityException("Authentication failed");
        }
    }
}
