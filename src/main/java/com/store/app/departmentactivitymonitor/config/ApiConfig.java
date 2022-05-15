package com.store.app.departmentactivitymonitor.config;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("api")
public class ApiConfig {

    private String userAgent;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("userAgent", userAgent)
                          .toString();
    }
}
