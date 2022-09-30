package com.xxl.job.admin.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(name = "xxl_job_group")
public class XxlJobGroup {

	@Id
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "native", parameters = {
			@org.hibernate.annotations.Parameter(name = "sequence_name", value = "s_xxl_job_group") })
	@Column(name = "id", nullable = false, unique = true)
	private int id;
	@Column(name = "app_name", length = 64, nullable = false)
	@Comment("执行器AppName")
	private String appname;
	@Column(name = "title", length = 24, nullable = false)
	@Comment("执行器名称")
	private String title;
	@Column(name = "address_type", nullable = false)
	@ColumnDefault("0")
	@Comment("执行器地址类型：0=自动注册、1=手动录入")
	private int addressType; // 执行器地址类型：0=自动注册、1=手动录入
	@Lob
	@Column(name = "address_list", length = 16 * 1024)
	@Comment("执行器地址列表，多地址逗号分隔")
	private String addressList; // 执行器地址列表，多地址逗号分隔(手动录入)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updateTime;

    // registry list
    @Transient
    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

}
