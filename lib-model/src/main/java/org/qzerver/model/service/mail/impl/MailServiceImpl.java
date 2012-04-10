package org.qzerver.model.service.mail.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.hibernate.validator.constraints.Email;
import org.qzerver.model.agent.mail.MailAgent;
import org.qzerver.model.agent.mail.MailAgentException;
import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.qzerver.model.service.mail.MailService;
import org.qzerver.system.template.TemplateEngine;
import org.qzerver.system.template.TemplateEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String NAME_JOB_FAILED = "job-failed";

    @NotNull
    @Email
    private String mailTo;

    @NotNull
    private MailAgent mailAgent;

    @NotNull
    private TemplateEngine templateEngine;

    @NotNull
    private boolean enabled;

    @NotNull
    private Locale locale;

    @NotNull
    private TimeZone timezone;

    @NotNull
    private MessageSourceAccessor messageSourceAccessor;

    @Async
    @Override
    public void notifyJobExecutionFailed(ScheduleExecution execution) {
        if (! enabled) return;

        Preconditions.checkNotNull(execution, "Execution is null");

        Map<String,Object> attributes = ImmutableMap.<String,Object>builder()
                .put("execution", execution)
                .build();

        String subject = messageSourceAccessor.getMessage("mail.subject." + NAME_JOB_FAILED, locale);
        String text;

        try {
            text = templateEngine.template(NAME_JOB_FAILED + ".ftl", attributes, locale, timezone);
        } catch (TemplateEngineException e) {
            LOGGER.error("Fail to render template for [" + NAME_JOB_FAILED + "]", e);
            return;
        }

        try {
            mailAgent.sendMail(mailTo, subject, text);
        } catch (MailAgentException e) {
            LOGGER.error("Fail to send message [" + NAME_JOB_FAILED + "]", e);
            return;
        }

        LOGGER.debug("Message [" + NAME_JOB_FAILED + "] is sent");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMailAgent(MailAgent mailAgent) {
        this.mailAgent = mailAgent;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }
}
