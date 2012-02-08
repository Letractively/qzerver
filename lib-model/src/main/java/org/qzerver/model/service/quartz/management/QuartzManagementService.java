package org.qzerver.model.service.quartz.management;

import org.springframework.stereotype.Service;

@Service
public interface QuartzManagementService {

    /**
     * Check current state of Quartz engine (current node only)
     * @return true if Quartz is active
     */
    boolean isActive();

    /**
     * Set Quartz state (current node only)
     * @param active state
     */
    void setActive(boolean active);


}
