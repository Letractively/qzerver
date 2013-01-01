package org.qzerver.util;

import org.apache.commons.lang.SystemUtils;

import java.util.Map;

public class SampleProgram {

    public static void main(String[] arguments) throws Exception {
        System.out.printf("START#\n");

        System.out.printf("WORKFOLDER#%s\n", SystemUtils.getUserDir());

        for (int i=0, size=arguments.length; i < size; i++) {
            System.out.printf("ARGUMENT_%d#%s\n", i+1, arguments[i]);
        }

        Map<String, String> environment = System.getenv();
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            System.out.printf("ENVIRONMENT#%s=%s\n", entry.getKey(), entry.getValue());
        }

        String error = System.getenv("SCRIPT_ERROR");
        if (error != null) {
            System.out.printf("ERROR#\n");
            System.err.printf("%s\n", error);
        }

        String sleep = System.getenv("SCRIPT_SLEEP");
        if (sleep != null) {
            System.out.printf("SLEEPING#\n");
            long sleepValueSec = Long.parseLong(sleep);
            Thread.sleep(sleepValueSec * 1000);
        }

        String lines = System.getenv("SCRIPT_LINES");
        if (lines != null) {
            System.out.printf("DUMPING#\n");
            int linesValue = Integer.parseInt(lines);
            for (int i=0; i < linesValue; i++) {
                System.out.printf("01234567890123456789012345678901234567890123456789");
            }
        }

        System.out.printf("FINISH#\n");

        String exitCode = System.getenv("SCRIPT_EXITCODE");
        if (exitCode != null) {
            int exitCodeValue = Integer.parseInt(exitCode);
            System.exit(exitCodeValue);
        }
    }
}
