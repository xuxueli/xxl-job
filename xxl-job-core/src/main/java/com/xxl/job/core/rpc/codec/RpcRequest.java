package com.xxl.job.core.rpc.codec;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * request
 * @author xuxueli 2015-10-29 19:39:12
 */
@Data
public class RpcRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String serverAddress;
	private long createMillisTime;

    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
