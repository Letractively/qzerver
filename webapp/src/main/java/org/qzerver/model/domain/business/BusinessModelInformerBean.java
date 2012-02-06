package org.qzerver.model.domain.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class BusinessModelInformerBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessModelInformerBean.class);

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Business model version is {}", BusinessModel.VERSION);
    }

}
