package org.qzerver.model.service.quartz.management;

import org.springframework.stereotype.Service;

/**
 * Quartz engine management
 */
@Service
public interface QuartzManagementService {

    /**
     * Check current state of Quartz engine (current node only)
     * @return true if Quartz is active
     */
    boolean isActive();

    /**
     * Set Quartz state (current node only)
     * @param active state
     */
    void setActive(boolean active);

    /**
     * Create quartz job
     * @param jobId Unique job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     * @param enabled Does the job should be started right after creation
     */
    void createJob(long jobId, String cron, String timeZoneId, boolean enabled);

    /**
     * Delete job
     * @param jobId Job identifier
     */
    void deleteJob(long jobId);

    /**
     * Change cron expression of the exising job
     * @param jobId Job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     */
    void rescheduleJob(long jobId, String cron, String timeZoneId);

    /**
     * Enable the existing job with cron expression (create the trigger for job)
     * @param jobId Job identifier
     * @param cron Cron expression
     * @param timeZoneId Time zone identifier
     */
    void enableJob(long jobId, String cron, String timeZoneId);

    /**
     * Disable the existing job (delete all job triggers)
     * @param jobId Job identifier
     */
    void disableJob(long jobId);

    /**
     * Check is job enabled
     * @param jobId Jon identifier
     * @return Returns true if job is enabled
     */
    boolean isJobEnabled(long jobId);

}
