package com.xxl.job.admin.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "xxl_job_log_report", indexes = { @Index(name = "i_trigger_day", columnList = "trigger_day", unique = true) })
public class XxlJobLogReport {

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "native", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequence_name", value = "s_xxl_job_log_report") })
	@Column(name = "id", nullable = false, unique = true)
    private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "trigger_day")
	@Comment("调度-时间")
    private Date triggerDay;

	@Column(name = "running_count", nullable = false)
	@ColumnDefault("0")
	@Comment("运行中-日志数量")
    private int runningCount;
	@Column(name = "suc_count", nullable = false)
	@ColumnDefault("0")
	@Comment("执行成功-日志数量")
    private int sucCount;
	@Column(name = "fail_count", nullable = false)
	@ColumnDefault("0")
	@Comment("执行失败-日志数量")
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
