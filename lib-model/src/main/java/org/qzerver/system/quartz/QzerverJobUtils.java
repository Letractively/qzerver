package org.qzerver.system.quartz;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.qzerver.model.domain.entities.job.ScheduleJob;

public final class QzerverJobUtils {

    public static final String QZERVER_GROUP = "QZERVER_GROUP";

    public static final String QZERVER_TRIGGER = "QZERVER_TRIGGER";

    private QzerverJobUtils() {
    }

    /**
     * Parse Quartz job name to ScheduleJob identifier
     * @param name Quartz job name
     * @return Identifier
     */
    public static long parseJobName(String name) {
        return Long.parseLong(name);
    }

    /**
     * Format Quatz job name from ScheduleJob identifier
     * @param id Identifier
     * @return Quartz job name
     */
    public static String formatJobName(long id) {
        return Long.toString(id);
    }

    public static TriggerKey triggerKey(ScheduleJob scheduleJob) {
        return TriggerKey.triggerKey(formatJobName(scheduleJob.getId()), QZERVER_GROUP);
    }

    public static JobKey jobKey(ScheduleJob scheduleJob) {
        return JobKey.jobKey(formatJobName(scheduleJob.getId()), QZERVER_GROUP);
    }
}
