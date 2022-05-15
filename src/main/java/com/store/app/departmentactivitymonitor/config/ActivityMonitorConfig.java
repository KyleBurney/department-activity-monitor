package com.store.app.departmentactivitymonitor.config;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("activityMonitor")
public class ActivityMonitorConfig {

    private long minContourArea;

    private long binarizationThreshold;

    private long departmentWidth;

    private long departmentHeight;

    private int numFramesToAverage;

    private long delayBetweenFrames;

    public long getMinContourArea() {
        return minContourArea;
    }

    public void setMinContourArea(long minContourArea) {
        this.minContourArea = minContourArea;
    }

    public long getBinarizationThreshold() {
        return binarizationThreshold;
    }

    public void setBinarizationThreshold(long binarizationThreshold) {
        this.binarizationThreshold = binarizationThreshold;
    }

    public long getDepartmentWidth() {
        return departmentWidth;
    }

    public void setDepartmentWidth(long departmentWidth) {
        this.departmentWidth = departmentWidth;
    }

    public long getDepartmentHeight() {
        return departmentHeight;
    }

    public void setDepartmentHeight(long departmentHeight) {
        this.departmentHeight = departmentHeight;
    }

    public int getNumFramesToAverage() {
        return numFramesToAverage;
    }

    public void setNumFramesToAverage(int numFramesToAverage) {
        this.numFramesToAverage = numFramesToAverage;
    }

    public long getDelayBetweenFrames() {
        return delayBetweenFrames;
    }

    public void setDelayBetweenFrames(long delayBetweenFrames) {
        this.delayBetweenFrames = delayBetweenFrames;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("minContourArea", minContourArea)
                          .add("binarizationThreshold", binarizationThreshold)
                          .add("departmentWidth", departmentWidth)
                          .add("departmentHeight", departmentHeight)
                          .add("numFramesToAverage", numFramesToAverage)
                          .add("delayBetweenFrames", delayBetweenFrames)
                          .toString();
    }
}
