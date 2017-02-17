package com.xxl.job.admin.core.model;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class ReturnT<T> {
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	
	private int code;
	private int size;
	private String msg;
	private T content;
	
	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public ReturnT(T content) {
		this.code = 200;
		this.content = content;
	}
	public ReturnT(T content,int size) {
		this.code = 200;
		this.size=size;
		this.content = content;
	}
	public ReturnT(T content,String msg,int size) {
		this.code = 200;
		this.size=size;
		this.msg=msg;
		this.content = content;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", content="
				+ content + "]";
	}

}
