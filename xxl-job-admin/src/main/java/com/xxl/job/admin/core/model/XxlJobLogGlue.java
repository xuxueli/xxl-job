package com.xxl.job.admin.core.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 * @author xuxueli 2016-5-19 17:57:46
 */
@Entity
@Table(name = "xxl_job_logglue")
public class XxlJobLogGlue {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityGenerator") // 使用基于雪花算法的主键生成策略
	@GenericGenerator(name = "IdentityGenerator", strategy = "com.xxl.job.admin.core.util.XxlJobGenerator")
	private Long id;
	@Column(name = "job_id", nullable = false, length = 20)
	private Long jobId;				// 任务主键ID
	@Column(name = "glue_type", length = 50)
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	@Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name = "glue_source")
	private String glueSource;
	@Column(name = "glue_remark", length = 128)
	private String glueRemark;
	@Column(name = "add_time")
	private Date addTime;
	@Column(name = "update_time")
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
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

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
