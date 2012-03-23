package org.qzerver.system.template.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.qzerver.system.template.TemplateEngine;
import org.qzerver.system.template.TemplateEngineException;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FreemarkerTemplateEngine implements TemplateEngine {

    private Configuration configuration;

    @Override
    public String template(String name, Map<String, Object> attributes, Locale locale, TimeZone timezone) throws TemplateEngineException {
        Preconditions.checkNotNull(name, "Name is null");
        Preconditions.checkNotNull(locale, "Locale is not set");
        Preconditions.checkNotNull(timezone, "Time zone is not set");

        Template template;

        ImmutableMap.Builder<String, Object> modelBuilder = ImmutableMap.builder();
        if (attributes != null) {
            modelBuilder.putAll(attributes);
        }
        modelBuilder.put("locale", locale);
        modelBuilder.put("timezone", timezone);

        Map<String, Object> model = modelBuilder.build();

        try {
            template = configuration.getTemplate(name, locale);
        } catch (IOException e) {
            throw new TemplateEngineException("Fail to load template: " + name, e);
        }

        StringWriter writer = new StringWriter();
        try {
            template.process(model, writer);
        } catch (IOException e) {
            throw new TemplateEngineException("Fail to process template: " + name, e);
        } catch (TemplateException e) {
            throw new TemplateEngineException("Fail to process template: " + name, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }

        return writer.toString();
    }

    @Required
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}