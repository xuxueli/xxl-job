package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行器注册表
 *
 * @author xuxueli  2016-9-30
 */
@TableName("xxl_job_registry")
public class XxlJobRegistry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 注册分组
     */
    private String registryGroup;

    /**
     * 注册Key
     */
    private String registryKey;

    /**
     * 注册值
     */
    private String registryValue;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistryGroup() { return registryGroup; }
    public void setRegistryGroup(String registryGroup) { this.registryGroup = registryGroup; }

    public String getRegistryKey() { return registryKey; }
    public void setRegistryKey(String registryKey) { this.registryKey = registryKey; }

    public String getRegistryValue() { return registryValue; }
    public void setRegistryValue(String registryValue) { this.registryValue = registryValue; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}