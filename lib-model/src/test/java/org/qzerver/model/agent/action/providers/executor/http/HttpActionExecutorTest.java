package org.qzerver.model.agent.action.providers.executor.http;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.qzerver.base.AbstractModelTest;
import org.qzerver.model.agent.action.providers.ActionExecutor;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpActionExecutorTest extends AbstractModelTest {

    private static final int HTTP_SERVER_PORT = 3001;

    @Resource
    private ActionExecutor httpActionExecutor;

    private Server server;

    @Before
    public void setUp() throws Exception {
        RequestHandler requestHandler = new RequestHandler();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setHost("127.0.0.1");
        connector.setPort(HTTP_SERVER_PORT);
        connector.setName("admin");

        server = new Server(HTTP_SERVER_PORT);
        server.setHandler(requestHandler);
        server.setConnectors(new Connector[]{connector});
        server.setStopAtShutdown(true);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testName1() throws Exception {
        Thread.sleep(30000);
    }

    @Test
    public void testName2() throws Exception {
        Thread.sleep(30000);
    }

    private static class RequestHandler extends AbstractHandler {

        @Override
        public void handle(String s, Request request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException, ServletException
        {
            httpServletResponse.setContentType("text/html;charset=utf-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.getWriter().println("<html><body>ack</body></html");
            request.setHandled(true);
        }
    }
}
