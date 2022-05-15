package com.store.app.departmentactivitymonitor.config;

import com.google.common.base.MoreObjects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class AppConfig {

    @Value("${spring.profiles}")
    private String environment;

    @Value("${application.processorCount}")
    private int processorCount;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public void setProcessorCount(int processorCount) {
        this.processorCount = processorCount;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("environment", environment)
                          .add("processorCount", processorCount)
                          .toString();
    }
}
