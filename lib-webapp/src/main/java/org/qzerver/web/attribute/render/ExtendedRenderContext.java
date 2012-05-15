package org.qzerver.web.attribute.render;

import com.gainmatrix.lib.web.attribute.render.RenderContext;

/**
 * Context for rendering web page
 */
public class ExtendedRenderContext extends RenderContext {

    private int businessModelVersion;

    private String web;

    public int getBusinessModelVersion() {
        return businessModelVersion;
    }

    public void setBusinessModelVersion(int businessModelVersion) {
        this.businessModelVersion = businessModelVersion;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

}
