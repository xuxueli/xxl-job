package com.xxl.job.core.router.model;

/**
 * Created by xuxueli on 16/7/22.
 */
public class ResponseModel {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String MORE = "MORE";

    private String status;
    private String msg;
    private int size;

    public ResponseModel() {
    }

    public ResponseModel(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    public ResponseModel(String status, String msg,int size) {
    	this.status = status;
    	this.msg = msg;
    	this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
    public String toString() {
        return "ResponseModel{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

}
