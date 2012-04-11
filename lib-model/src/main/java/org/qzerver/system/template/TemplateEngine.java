package org.qzerver.system.template;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public interface TemplateEngine {

    /**
     * Generate text from template and attributes
     * @param name Name of template
     * @param attributes Map of atributes
     * @param locale Locale
     * @param timezone Timezone
     * @return Text
     * @throws TemplateEngineException Exception on error
     */
    String template(String name, Map<String,Object> attributes, Locale locale, TimeZone timezone)
            throws TemplateEngineException;

}
