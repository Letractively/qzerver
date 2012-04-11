package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.spring.i18n.ClientI18nResolver;
import com.gainmatrix.lib.time.Chronometer;
import com.gainmatrix.lib.web.attribute.AttributePublisher;
import org.qzerver.model.domain.business.BusinessModel;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

public class RenderContextAttributePublisher implements AttributePublisher {

    public static final String ATTRIBUTE_NAME = "renderContext";

    @NotNull
    private String web;

    @NotNull
    private Chronometer chronometer;

    @NotNull
    private ClientI18nResolver clientI18nResolver;

    @Override
    public void publish(HttpServletRequest request) {
        RenderContext renderContext = new RenderContext();
        renderContext.setNow(chronometer.getCurrentMoment());
        renderContext.setWeb(web);
        renderContext.setRevision(BusinessModel.VERSION);
        renderContext.setLocale(clientI18nResolver.getLocale());
        renderContext.setTimezone(clientI18nResolver.getTimeZone());

        request.setAttribute(ATTRIBUTE_NAME, renderContext);
    }

    @Required
    public void setChronometer(Chronometer chronometer) {
        this.chronometer = chronometer;
    }

    @Required
    public void setClientI18nResolver(ClientI18nResolver clientI18nResolver) {
        this.clientI18nResolver = clientI18nResolver;
    }

    @Required
    public void setWeb(String web) {
        this.web = web;
    }

}
