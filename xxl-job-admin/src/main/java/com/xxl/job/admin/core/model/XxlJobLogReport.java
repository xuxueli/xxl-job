package com.xxl.job.admin.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "job_log_report")
@TableGenerator(name = "job_log_report_gen",
        table="primary_key_gen",
        pkColumnName="gen_name",
        valueColumnName="gen_value",
        pkColumnValue="JOB_LOG_REPORT_PK",
        allocationSize=1
)
public class XxlJobLogReport {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="job_log_report_gen")
    @Column(length = 11,nullable = false)
    private int id;

    @Column(name = "trigger_day")
    private Date triggerDay;

    @Column(name = "running_count",length = 11,nullable = false)
    private int runningCount;
    @Column(name = "suc_count",length = 11,nullable = false)
    private int sucCount;
    @Column(name = "fail_count",length = 11,nullable = false)
    private int failCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTriggerDay() {
        return triggerDay;
    }

    public void setTriggerDay(Date triggerDay) {
        this.triggerDay = triggerDay;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getSucCount() {
        return sucCount;
    }

    public void setSucCount(int sucCount) {
        this.sucCount = sucCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
}
