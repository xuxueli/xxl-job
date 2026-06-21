package com.xxl.job.core.openapi.admin.dto;

import java.io.Serializable;

/**
 * request DTO for adding a new job
 */
public class JobAddRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    private int jobGroup;
    private String jobDesc;

    private String author;
    private String alarmEmail;

    private String scheduleType;                    // NONE、CRON、FIX_RATE
    private String scheduleConf;
    private String misfireStrategy;                 // DO_NOTHING、FIRE_ONCE_NOW

    private String executorRouteStrategy;           // FIRST、LAST、ROUND、RANDOM、CONSISTENT_HASH、LEAST_FREQUENTLY_USED、LEAST_RECENTLY_USED、FAILOVER、BUSYOVER、SHARDING_BROADCAST
    private String executorHandler;
    private String executorParam;
    private String executorBlockStrategy;           // SERIAL_EXECUTION、DISCARD_LATER、COVER_EARLY
    private int executorTimeout;
    private int executorFailRetryCount;

    private String glueType;                        // BEAN、GLUE_GROOVY、GLUE_SHELL、GLUE_PYTHON、GLUE_NODEJS、GLUE_POWERSHELL、GLUE_PHP
    private String glueSource;
    private String glueRemark;

    public JobAddRequest() {
    }

    public int getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(int jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public String getMisfireStrategy() {
        return misfireStrategy;
    }

    public void setMisfireStrategy(String misfireStrategy) {
        this.misfireStrategy = misfireStrategy;
    }

    public String getExecutorRouteStrategy() {
        return executorRouteStrategy;
    }

    public void setExecutorRouteStrategy(String executorRouteStrategy) {
        this.executorRouteStrategy = executorRouteStrategy;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }

    public int getExecutorTimeout() {
        return executorTimeout;
    }

    public void setExecutorTimeout(int executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    public int getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public void setExecutorFailRetryCount(int executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
    }

    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }

    public String getGlueRemark() {
        return glueRemark;
    }

    public void setGlueRemark(String glueRemark) {
        this.glueRemark = glueRemark;
    }

}
