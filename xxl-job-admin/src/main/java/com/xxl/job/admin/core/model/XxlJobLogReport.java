package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "xxl_job_log_report", indexes = {
        @Index(name = "i_trigger_day", columnList = "trigger_day", unique = true)})
public class XxlJobLogReport {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
    @GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
    private Long id;

    @Column(name = "trigger_day")
    private Date triggerDay;

    @Column(name = "running_count", nullable = false, length = 11)
    private int runningCount;
    @Column(name = "suc_count", nullable = false, length = 11)
    private int sucCount;
    @Column(name = "fail_count", nullable = false, length = 11)
    private int failCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
