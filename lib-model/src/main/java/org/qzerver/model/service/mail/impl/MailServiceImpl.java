package org.qzerver.model.service.mail.impl;

import org.qzerver.model.service.mail.MailService;
import org.springframework.scheduling.annotation.Async;

public class MailServiceImpl implements MailService {

    @Async
    @Override
    public void notifyJobFailed() {
    }

}
