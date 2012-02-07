package org.qzerver.model.service.quartz.management;

import org.springframework.stereotype.Service;

@Service
public interface QuartzManagementService {

    boolean isActive();

    void setActive(boolean active);


}
