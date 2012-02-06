package org.qzerver.system.quartz;

/**
 * Abstract job executor
 */
public interface QzerverJobExecutor {

    /**
     * Execute Quartz job with the specified id
     * @param quartzJobName quartz job name
     * @param quartzJobGroup quartz job group
     */
    void executeJob(String quartzJobName, String quartzJobGroup);

}
