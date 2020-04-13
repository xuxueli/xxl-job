package com.xxl.job.admin.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 * @author xuxueli 2016-5-19 17:57:46
 */
@Entity
@Table(name = "job_logglue")
@TableGenerator(name = "job_logglue_gen",
		table="primary_key_gen",
		pkColumnName="gen_name",
		valueColumnName="gen_value",
		pkColumnValue="JOB_LOGGLUE_PK",
		allocationSize=1
)
public class XxlJobLogGlue {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="job_logglue_gen")
	@Column(length = 11,nullable = false)
	private int id;
	@Column(name = "job_id",length = 11,nullable = false)
	private int jobId;				// 任务主键ID
	@Column(name = "glue_type",length = 50)
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	@Lob
	@Column(name = "glue_source")
	private String glueSource;
	@Column(name = "glue_remark",length = 128,nullable = false)
	private String glueRemark;
	@Column(name = "add_time")
	private Date addTime;
	@Column(name = "update_time")
	private Date updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
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
