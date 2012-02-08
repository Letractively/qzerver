package org.qzerver.model.service.job.impl;

import org.qzerver.model.service.job.JobManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NEVER)
public class JobManagementServiceImpl implements JobManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManagementServiceImpl.class);
}
