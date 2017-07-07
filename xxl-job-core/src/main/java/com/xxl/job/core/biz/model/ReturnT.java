package com.xxl.job.core.biz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;

	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;
	public static final ReturnT<String> SUCCESS = new ReturnT<>(null);
	public static final ReturnT<String> FAIL = new ReturnT<>(FAIL_CODE, null);
	
	private int code;
	private String msg;
	private T content;

	public ReturnT(int code, T content) {
		this.code = code;
		this.content = content;
	}

	public ReturnT(T content) {
		this.code = SUCCESS_CODE;
		this.content = content;
	}

	@JsonIgnore
	public boolean isSuccess() {
		return this.code == SUCCESS_CODE;
	}

	public static <T> ReturnT<T> success(T content) {
		return new ReturnT<>(SUCCESS_CODE, content);
	}

	public static <T> ReturnT<T> error(String msg) {
		return new ReturnT<>(FAIL_CODE, msg, null);
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", content=" + content + "]";
	}

}
