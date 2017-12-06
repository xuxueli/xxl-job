package com.xxl.job.executor.service.pojo;

import java.io.Serializable;
import java.util.Date;

public class RealTimeJob implements Serializable {
	private static final long serialVersionUID = 2336237798759634903L;
	private int id;
	private String name;
	private int status;
	private double price;
	private boolean flag;
	private long total;
	private int priority;
	private String code;
	private String msg;
	private String content;
	private int retry_times;
	private int max_retry_times;
	private Date updatetime;
	private Date createtime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getRetry_times() {
		return retry_times;
	}
	public void setRetry_times(int retry_times) {
		this.retry_times = retry_times;
	}
	public int getMax_retry_times() {
		return max_retry_times;
	}
	public void setMax_retry_times(int max_retry_times) {
		this.max_retry_times = max_retry_times;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}
