package org.qzerver.web.controller.system;

import java.io.Serializable;

public class SystemStateModel implements Serializable {

    private boolean scheduleActive;

    public boolean isScheduleActive() {
        return scheduleActive;
    }

    public void setScheduleActive(boolean scheduleActive) {
        this.scheduleActive = scheduleActive;
    }
}
