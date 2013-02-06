package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;

/**
 * Context for rendering web page
 */
public class ExtendedRenderContext extends RenderContext {

    private int businessModelVersion;

    private String url;

    private boolean development;

    public int getBusinessModelVersion() {
        return businessModelVersion;
    }

    public void setBusinessModelVersion(int businessModelVersion) {
        this.businessModelVersion = businessModelVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDevelopment() {
        return development;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
    }
}
