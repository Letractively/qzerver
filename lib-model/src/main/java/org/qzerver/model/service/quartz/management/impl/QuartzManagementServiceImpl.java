package org.qzerver.model.service.quartz.management.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Transactional(propagation = Propagation.REQUIRED)
public class QuartzManagementServiceImpl implements QuartzManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManagementServiceImpl.class);

    @NotNull
    private Scheduler scheduler;

    @Override
    public boolean isActive() {
        try {
            return ! scheduler.isInStandbyMode();
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to check quartz activity", e);
        }
    }

    @Override
    public void setActive(boolean active) {
        LOGGER.debug("Set quartz state = {}", active);

        try {
            if (active) {
                if (scheduler.isInStandbyMode()) {
                    scheduler.start();
                }
            } else {
                if (! scheduler.isInStandbyMode()) {
                    scheduler.standby();
                }
            }
        } catch (SchedulerException e) {
            throw new SystemIntegrityException("Fail to toggle quartz activity", e);
        }
    }

    @Required
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
