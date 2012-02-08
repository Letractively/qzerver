package org.qzerver.model.service.quartz.executor.impl;

import com.gainmatrix.lib.spring.validation.BeanValidationUtils;
import org.qzerver.model.service.quartz.executor.QuartzExecutorService;
import org.qzerver.model.service.quartz.executor.dto.QuartzExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;


@Transactional(propagation = Propagation.NEVER)
public class QuartzExecutorServiceImpl implements QuartzExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzExecutorServiceImpl.class);

    private Validator beanValidator;

    @Override
    public void executeJob(QuartzExecutionParameters parameters) {
        BeanValidationUtils.checkValidity(parameters, beanValidator);

        LOGGER.debug("Job [id={}] is about to execute", parameters.getId());
    }

    @Required
    public void setBeanValidator(Validator beanValidator) {
        this.beanValidator = beanValidator;
    }
}
