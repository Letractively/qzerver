package org.qzerver.model.agent.mail.impl;

import org.hibernate.validator.constraints.Email;
import org.qzerver.model.agent.mail.MailAgent;
import org.qzerver.model.agent.mail.MailAgentException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.validation.constraints.NotNull;

public class MailAgentImpl implements MailAgent {

    @NotNull
    private JavaMailSender javaMailSender;

    @NotNull
    @Email
    private String emailFrom;

    @Override
    public void sendMail(String emailTo, String subject, String text) throws MailAgentException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(text);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new MailAgentException("Fail to send a message", e);
        }
    }

    @Required
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Required
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }
}
