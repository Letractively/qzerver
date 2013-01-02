package org.qzerver.model.agent.action.providers.executor.jmx.beans;

public class SampleJmxService implements SampleJmxServiceMBean {

    @Override
    public String method1(String arg1, String arg2) {
        StringBuilder sb = new StringBuilder();
        sb.append(arg1);
        sb.append(arg2);
        return sb.toString();
    }

    @Override
    public Long method2(String arg1) {
        return Long.valueOf(arg1);
    }

}
