package org.qzerver.web.attribute.render;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Context for rendering web page
 */
public class RenderContext implements Serializable {

    private int businessModelVersion;

    private String applicationVersion;

    private Locale locale;

    private TimeZone timezone;

    private Date now;

    private String web;

    public int getBusinessModelVersion() {
        return businessModelVersion;
    }

    public void setBusinessModelVersion(int businessModelVersion) {
        this.businessModelVersion = businessModelVersion;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
}
