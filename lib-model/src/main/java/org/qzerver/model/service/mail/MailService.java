package org.qzerver.model.service.mail;

import org.springframework.stereotype.Service;

@Service
public interface MailService {

    void notifyJobFailed();

}
