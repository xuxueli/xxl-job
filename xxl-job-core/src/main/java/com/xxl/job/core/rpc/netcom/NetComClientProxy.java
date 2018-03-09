package com.xxl.job.core.rpc.netcom;

import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.jetty.client.JettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * rpc proxy
 * @author xuxueli 2015-10-29 20:18:32
 */
public class NetComClientProxy implements FactoryBean<Object> {
	private static final Logger logger = LoggerFactory.getLogger(NetComClientProxy.class);

	// ---------------------- config ----------------------
	private Class<?> iface;
	private String serverAddress;
	private String accessToken;
	private JettyClient client = new JettyClient();
	public NetComClientProxy(Class<?> iface, String serverAddress, String accessToken) {
		this.iface = iface;
		this.serverAddress = serverAddress;
		this.accessToken = accessToken;
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

						// filter method like "Object.toString()"
						if (Object.class.getName().equals(method.getDeclaringClass().getName())) {
							logger.error(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}.{}]", method.getDeclaringClass().getName(), method.getName());
							throw new RuntimeException("xxl-rpc proxy class-method not support");
						}
						
						// request
						RpcRequest request = new RpcRequest();
	                    request.setServerAddress(serverAddress);
	                    request.setCreateMillisTime(System.currentTimeMillis());
	                    request.setAccessToken(accessToken);
	                    request.setClassName(method.getDeclaringClass().getName());
	                    request.setMethodName(method.getName());
	                    request.setParameterTypes(method.getParameterTypes());
	                    request.setParameters(args);

	                    // send
	                    RpcResponse response = client.send(request);
	                    
	                    // valid response
						if (response == null) {
							throw new Exception("Network request fail, response not found.");
						}
	                    if (response.isError()) {
	                        throw new RuntimeException(response.getError());
	                    } else {
	                        return response.getResult();
	                    }
	                   
					}
				});
	}
	@Override
	public Class<?> getObjectType() {
		return iface;
	}
	@Override
	public boolean isSingleton() {
		return false;
	}

}
