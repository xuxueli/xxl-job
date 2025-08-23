package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * common return
 *
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	private int code;

	private String msg;

	private T content;

	public ReturnT(){}

	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ReturnT(int code, String msg, T content) {
		this.code = code;
		this.msg = msg;
		this.content = content;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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
		return "ReturnT{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", content=" + content +
				'}';
	}


	// --------------------------- tool ---------------------------

	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;

	/**
	 * is success
	 *
	 * @return
	 */
	public boolean isSuccess() {
		return code == SUCCESS_CODE;
	}

	public static <T> ReturnT<T> of(int code, String msg, T data) {
		return new ReturnT<T>(code, msg, data);
	}
	public static <T> ReturnT<T> of(int code, String msg) {
		return new ReturnT<T>(code, msg, null);
	}

	public static <T> ReturnT<T> ofSuccess(T data) {
		return new ReturnT<T>(SUCCESS_CODE, "Success", data);
	}

	public static <T> ReturnT<T> ofSuccess() {
		return new ReturnT<T>(SUCCESS_CODE, "Success", null);
	}

	public static <T> ReturnT<T> ofFail(String msg) {
		return new ReturnT<T>(FAIL_CODE, msg, null);
	}
	public static <T> ReturnT<T> ofFail() {
		return new ReturnT<T>(FAIL_CODE, "Fail", null);
	}

}
