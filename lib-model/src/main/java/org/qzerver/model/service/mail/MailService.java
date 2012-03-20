package org.qzerver.model.service.mail;

import org.qzerver.model.domain.entities.job.ScheduleExecution;
import org.springframework.stereotype.Service;

@Service
public interface MailService {

    /**
     * Inform execution failed
     * @param execution Execution entity
     */
    void notifyJobExecutionFailed(ScheduleExecution execution);

}
