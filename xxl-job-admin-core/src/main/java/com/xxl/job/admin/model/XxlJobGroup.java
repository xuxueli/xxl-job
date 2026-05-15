package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xxl.tool.core.StringTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 执行器组
 *
 * @author xuxueli  2016-9-30
 */
@TableName("xxl_job_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxlJobGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appname;

    /**
     * 名称
     */
    @TableField("title")
    private String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @TableField("address_type")
    private int addressType;

    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     */
    @TableField("address_list")
    private String addressList;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * registry list (not in DB, computed field)
     */
    @TableField(exist = false)
    private List<String> registryList;

    /**
     * 获取执行器地址列表(系统注册)
     */
    public List<String> getRegistryList() {
        if (registryList == null && StringTool.isNotBlank(addressList)) {
            registryList = new ArrayList<>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAppname() { return appname; }
    public void setAppname(String appname) { this.appname = appname; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getAddressType() { return addressType; }
    public void setAddressType(int addressType) { this.addressType = addressType; }

    public String getAddressList() { return addressList; }
    public void setAddressList(String addressList) { this.addressList = addressList; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}