package com.xxl.job.core.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * response
 * @author xuxueli 2015-10-29 19:39:54
 */
@Data
public class RpcResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }
}
