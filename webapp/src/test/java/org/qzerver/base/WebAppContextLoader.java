package org.qzerver.base;

import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextLoader;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebAppContextLoader implements ContextLoader {

    private static final String CONTEXT_MODEL = "classpath:/configuration/context/model/root.xml";

    private static final String CONTEXT_WEBAPP = "classpath:/configuration/context/webapp/root.xml";

    private static final String CONTEXT_SERVLET = "classpath:/configuration/context/servlet/root.xml";

    private static final String CONTEXT_TEST_MODEL = "classpath:/test/context/test-model.xml";

    private static final String CONTEXT_TEST_SERVLET = "classpath:/test/context/test-servlet.xml";

    @Override
    public String[] processLocations(Class<?> clazz, String... locations) {
        return locations;
    }

    @Override
    public ApplicationContext loadContext(String... locations) throws Exception {
        // Listener context
        ClassPathXmlApplicationContext modelContext = new ClassPathXmlApplicationContext();
        modelContext.setConfigLocations(new String[] {
                CONTEXT_MODEL, CONTEXT_WEBAPP, CONTEXT_TEST_MODEL
        });
        modelContext.setAllowCircularReferences(true);
        modelContext.setAllowBeanDefinitionOverriding(true);
        modelContext.setValidating(true);
        modelContext.refresh();

        // Remove the contexts we already know about
        Set<String> localLocations = Sets.newHashSet(locations);
        localLocations.remove(CONTEXT_MODEL);
        localLocations.remove(CONTEXT_WEBAPP);
        localLocations.remove(CONTEXT_SERVLET);
        localLocations.remove(CONTEXT_TEST_MODEL);
        localLocations.remove(CONTEXT_TEST_SERVLET);

        // Servlet context
        List<String> servletLocations = new ArrayList<String>();
        servletLocations.add(CONTEXT_SERVLET);
        servletLocations.add(CONTEXT_TEST_SERVLET);
        servletLocations.addAll(localLocations);

        XmlWebApplicationContext servletContext = new XmlWebApplicationContext();
        servletContext.setParent(modelContext);
        servletContext.setConfigLocations(servletLocations.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        servletContext.setAllowCircularReferences(true);
        servletContext.setAllowBeanDefinitionOverriding(true);

        MockServletContext mockServletContext = new MockServletContext();
        servletContext.setServletContext(mockServletContext);

        MockServletConfig mockServletConfig = new MockServletConfig(mockServletContext, "springServlet");
        servletContext.setServletConfig(mockServletConfig);

        servletContext.refresh();

        return servletContext;
    }

}
