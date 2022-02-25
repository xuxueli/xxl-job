package com.xxl.job.admin.core.model;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
public class XxlJobAlarm {

    /**
     * 主键
     */
    private long id;

    /**
     * 任务主键
     */
    private int jobId;

    /**
     * 报警类型，对应报警插件名
     */
    private String alarmType;

    /**
     * 报警对象，例如收件人列表、手机号、httpUrl等
     */
    private String alarmTarget;

    /**
     * 详细配置，例如钉钉告警webhook，http告警自定义header
     */
    private String alarmConfig;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmTarget() {
        return alarmTarget;
    }

    public void setAlarmTarget(String alarmTarget) {
        this.alarmTarget = alarmTarget;
    }

    public String getAlarmConfig() {
        return alarmConfig;
    }

    public void setAlarmConfig(String alarmConfig) {
        this.alarmConfig = alarmConfig;
    }
}
