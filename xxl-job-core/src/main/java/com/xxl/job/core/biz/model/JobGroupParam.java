package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * job 分组
 *
 * @author L.cm
 */
public class JobGroupParam implements Serializable {
	private static final long serialVersionUID = 42L;

	/**
	 * 英文标识，默认为服务名
	 */
	private String appName;
	/**
	 * 中文描述，默认为服务名
	 */
	private String title;
	/**
	 * 执行器地址类型：0=自动注册、1=手动录入
	 */
	private int addressType = 0;
	/**
	 * 执行器地址列表，多地址逗号分隔(手动录入)
	 */
	private String addressList;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public void setAddressList(String addressList) {
		this.addressList = addressList;
	}
}
