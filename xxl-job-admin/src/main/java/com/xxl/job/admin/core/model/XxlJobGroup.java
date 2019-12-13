package com.xxl.job.admin.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup implements Serializable {
	private static final long serialVersionUID = -5495070576851630863L;

	private int id;
	private String appName;
	private String title;
	private int order;
	private int addressType; // 执行器地址类型：0=自动注册、1=手动录入
	private String addressList; // 执行器地址列表，多地址逗号分隔(手动录入)

	// registry list
	private List<String> registryList; // 执行器地址列表(系统注册)

	public List<String> getRegistryList() {
		if (addressList != null && addressList.trim().length() > 0) {
			registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
		}
		return registryList;
	}

	public int getId() {
		return id;
	}

	public XxlJobGroup setId(int id) {
		this.id = id;
		return this;
	}

	public String getAppName() {
		return appName;
	}

	public XxlJobGroup setAppName(String appName) {
		this.appName = appName;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public XxlJobGroup setTitle(String title) {
		this.title = title;
		return this;
	}

	public int getOrder() {
		return order;
	}

	public XxlJobGroup setOrder(int order) {
		this.order = order;
		return this;
	}

	public int getAddressType() {
		return addressType;
	}

	public XxlJobGroup setAddressType(int addressType) {
		this.addressType = addressType;
		return this;
	}

	public String getAddressList() {
		return addressList;
	}

	public XxlJobGroup setAddressList(String addressList) {
		this.addressList = addressList;
		return this;
	}

}
