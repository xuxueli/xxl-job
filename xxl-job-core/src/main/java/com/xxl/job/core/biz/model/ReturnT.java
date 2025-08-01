package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;

	public static final ReturnT<String> SUCCESS = success();
	public static final ReturnT<String> FAIL = fail();

	private final int code;
	private final String msg;
	private final T content;

	public static <T> ReturnT<T> success() {
		return new ReturnT<>(SUCCESS_CODE, null, null);
	}

	public static <T> ReturnT<T> success(T content) {
		return new ReturnT<>(SUCCESS_CODE, null, content);
	}

	public static <T> ReturnT<T> fail() {
		return new ReturnT<>(FAIL_CODE, null, null);
	}

	public static <T> ReturnT<T> fail(String msg) {
		return new ReturnT<>(FAIL_CODE, msg, null);
	}

	public static <T> ReturnT<T> fail(int code, String msg) {
		return new ReturnT<>(code, msg, null);
	}

	public ReturnT(int code, String msg, T content) {
		this.code = code;
		this.msg = msg;
		this.content = content;
	}
	
	public int getCode() {
		return code;
	}
	public ReturnT<T> withCode(int code) {
		return new ReturnT<>(code, this.msg, this.content);
	}
	public String getMsg() {
		return msg;
	}
	public ReturnT<T> withMsg(String msg) {
		return new ReturnT<>(this.code, msg, this.content);
	}
	public T getContent() {
		return content;
	}
	public ReturnT<T> withContent(T content) {
		return new ReturnT<>(this.code, this.msg, content);
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
	}

}
