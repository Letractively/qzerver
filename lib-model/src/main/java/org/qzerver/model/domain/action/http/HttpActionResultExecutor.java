package org.qzerver.model.domain.action.http;

import org.qzerver.model.domain.action.ActionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpActionResultExecutor implements ActionExecutor<HttpActionResult, HttpActionDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpActionResultExecutor.class);

    @Override
    public HttpActionResult execute(HttpActionDefinition definition, String nodeAddress) {
        return null;
    }

}
