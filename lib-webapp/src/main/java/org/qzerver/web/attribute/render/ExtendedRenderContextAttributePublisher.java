package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;
import com.gainmatrix.lib.web.attribute.render.RenderContextAttributePublisher;
import org.qzerver.model.domain.business.BusinessModelVersionHolder;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotNull;

public class ExtendedRenderContextAttributePublisher extends RenderContextAttributePublisher {

    @NotNull
    private String web;

    @Override
    protected RenderContext createRenderContext() {
        return new ExtendedRenderContext();
    }

    @Override
    protected void publishRenderContext(RenderContext renderContext) {
        super.publishRenderContext(renderContext);

        ExtendedRenderContext extendedRenderContext = (ExtendedRenderContext) renderContext;
        extendedRenderContext.setWeb(web);
        extendedRenderContext.setBusinessModelVersion(BusinessModelVersionHolder.VERSION);
    }

    @Override
    public void setApplicationVersion(String applicationVersion) {
        super.setApplicationVersion(applicationVersion);
    }

    @Required
    public void setWeb(String web) {
        this.web = web;
    }

}
