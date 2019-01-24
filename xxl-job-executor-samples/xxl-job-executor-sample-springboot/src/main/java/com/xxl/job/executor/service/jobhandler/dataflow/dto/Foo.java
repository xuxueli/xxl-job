package com.xxl.job.executor.service.jobhandler.dataflow.dto;

public class Foo {
	private Integer id;
	/** 0： 待处理；1：已处理 */
	private Integer status;
	public Foo(Integer id, Integer status) {
		super();
		this.id = id;
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
