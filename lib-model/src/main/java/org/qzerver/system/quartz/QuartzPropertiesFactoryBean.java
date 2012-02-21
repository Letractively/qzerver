package org.qzerver.system.quartz;

import org.quartz.impl.jdbcjobstore.DriverDelegate;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;

public class QuartzPropertiesFactoryBean implements FactoryBean<Properties> {

    private String schema;

    private Properties properties;

    private String instanceName = "QZERVER";

    private String instanceId = "AUTO";

    private long misfireThresholdMs = 60000;

    private long clusterCheckinInterval = 30000;

    private Class<? extends DriverDelegate> driverDelegateClass;

    private int threadCount = 10;

    private int threadPriority = 5;

    private Map<Object, Object> additionalPropertyMap;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        properties = new Properties();

        // Fixed properties
        properties.put("org.quartz.scheduler.instanceName", instanceName);
        properties.put("org.quartz.scheduler.instanceId", instanceId);
        properties.put("org.quartz.jobStore.misfireThreshold", Long.toString(misfireThresholdMs));
        properties.put("org.quartz.jobStore.clusterCheckinInterval", Long.toString(clusterCheckinInterval));
        properties.put("org.quartz.jobStore.isClustered", Boolean.toString(true));
        properties.put("org.quartz.jobStore.driverDelegateClass", driverDelegateClass.getCanonicalName());
        properties.put("org.quartz.threadPool.threadCount", Integer.toString(threadCount));
        properties.put("org.quartz.threadPool.threadPriority", Integer.toString(threadPriority));
        properties.put("org.quartz.scheduler.skipUpdateCheck", Boolean.toString(true));

        // Schema and table prefix
        properties.put("org.quartz.jobStore.tablePrefix", (schema != null) ? schema + ".QRTZ_" : "QRTZ_");

        // Additional properties
        if (additionalPropertyMap != null) {
            properties.putAll(additionalPropertyMap);
        }
    }

    @Override
    public Properties getObject() throws Exception {
        return properties;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setAdditionalPropertyMap(Map<Object, Object> additionalPropertyMap) {
        this.additionalPropertyMap = additionalPropertyMap;
    }

    public void setClusterCheckinIntervalMs(long clusterCheckinInterval) {
        this.clusterCheckinInterval = clusterCheckinInterval;
    }

    public void setDriverDelegateClass(Class<? extends DriverDelegate> driverDelegateClass) {
        this.driverDelegateClass = driverDelegateClass;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setMisfireThresholdMs(long misfireThresholdMs) {
        this.misfireThresholdMs = misfireThresholdMs;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
