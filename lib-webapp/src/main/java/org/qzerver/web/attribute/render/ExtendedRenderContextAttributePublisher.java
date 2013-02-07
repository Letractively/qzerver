package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;
import com.gainmatrix.lib.web.attribute.render.RenderContextAttributePublisher;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotNull;

public class ExtendedRenderContextAttributePublisher extends RenderContextAttributePublisher {

    @NotNull
    private String url;

    private boolean development;

    @Override
    protected RenderContext createRenderContext() {
        return new ExtendedRenderContext();
    }

    @Override
    protected void publishRenderContext(RenderContext renderContext) {
        super.publishRenderContext(renderContext);

        ExtendedRenderContext extendedRenderContext = (ExtendedRenderContext) renderContext;
        extendedRenderContext.setUrl(url);
        extendedRenderContext.setDevelopment(development);
    }

    @Required
    public void setUrl(String url) {
        this.url = url;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
    }

}
