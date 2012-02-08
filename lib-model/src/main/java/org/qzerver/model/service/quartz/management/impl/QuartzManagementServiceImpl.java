package org.qzerver.model.service.quartz.management.impl;

import com.gainmatrix.lib.business.exception.SystemIntegrityException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.qzerver.model.service.quartz.management.QuartzManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public class QuartzManagementServiceImpl implements QuartzManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManagementServiceImpl.class);

    private final Scheduler scheduler;

    public QuartzManagementServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean isActive() {
        synchronized (scheduler) {
            try {
                return ! scheduler.isInStandbyMode();
            } catch (SchedulerException e) {
                throw new SystemIntegrityException("Fail to check quartz activity", e);
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        LOGGER.debug("Set quartz state = {}", active);

        synchronized (scheduler) {
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
    }

}
