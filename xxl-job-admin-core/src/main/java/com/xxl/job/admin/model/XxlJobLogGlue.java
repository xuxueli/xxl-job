package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 *
 * @author xuxueli  2016-5-19 17:57:46
 */
@TableName("xxl_job_logglue")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxlJobLogGlue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务主键ID
     */
    private int jobId;

    /**
     * GLUE类型
     */
    private String glueType;

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * GLUE备注
     */
    private String glueRemark;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private Date addTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public String getGlueType() { return glueType; }
    public void setGlueType(String glueType) { this.glueType = glueType; }

    public String getGlueSource() { return glueSource; }
    public void setGlueSource(String glueSource) { this.glueSource = glueSource; }

    public String getGlueRemark() { return glueRemark; }
    public void setGlueRemark(String glueRemark) { this.glueRemark = glueRemark; }

    public Date getAddTime() { return addTime; }
    public void setAddTime(Date addTime) { this.addTime = addTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}