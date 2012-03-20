package org.qzerver.model.agent.mail;

import org.springframework.stereotype.Service;

@Service
public interface MailAgent {

    /**
     * Send mail message
     * @param emailTo Target email address
     * @param subject Subject
     * @param text Message
     */
    void sendMail(String emailTo, String subject, String text) throws MailAgentException;

}
