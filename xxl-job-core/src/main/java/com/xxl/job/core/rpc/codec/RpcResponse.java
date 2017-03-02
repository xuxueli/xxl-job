package com.xxl.job.core.rpc.codec;

import java.io.Serializable;

/**
 * response
 * @author xuxueli 2015-10-29 19:39:54
 */
public class RpcResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

	@Override
	public String toString() {
		return "NettyResponse [error=" + error
				+ ", result=" + result + "]";
	}

}
