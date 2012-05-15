package org.qzerver.system.freemarker;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.validation.constraints.NotNull;

/**
 * Freemarker view resolver allows to specify configuration instance explicitly
 */
public class ConfiguredFreeMarkerViewResolver extends FreeMarkerViewResolver {

    @NotNull
    private Configuration configuration;

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        FreeMarkerView view = (FreeMarkerView) super.buildView(viewName);
        view.setConfiguration(configuration);
        return view;
    }

    @Required
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

}
