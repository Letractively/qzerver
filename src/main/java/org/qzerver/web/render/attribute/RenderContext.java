package org.qzerver.web.render.attribute;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Render context
 */
public class RenderContext implements Serializable {

    private int revision;

    private Locale locale;

    private TimeZone timezone;

    private Date now;

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
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
}
