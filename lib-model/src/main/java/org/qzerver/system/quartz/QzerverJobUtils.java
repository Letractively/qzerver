package org.qzerver.system.quartz;

public final class QzerverJobUtils {

    public static final String QZERVER_JOB_GROUP = "QZERVER_GROUP";

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

}
