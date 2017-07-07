package com.xxl.job.core.rpc.netcom;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.jetty.server.JettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * netcom init
 *
 * @author xuxueli 2015-10-31 22:54:27
 */
public class NetComServerFactory {
    private static final Logger logger = LoggerFactory.getLogger(NetComServerFactory.class);


    private static JettyServer server;
    private static Map<String, Object> serviceMap = new HashMap<>();

    public static void start(int port, String ip, String appName) throws Exception {
        server = new JettyServer();
        server.start(port, ip, appName);
    }

    // ---------------------- server destroy ----------------------
    public static void destroy() {
        server.destroy();
    }


    public static void putService(Class<?> iface, Object serviceBean) {
        serviceMap.put(iface.getName(), serviceBean);
    }

    public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
        if (serviceBean == null) {
            serviceBean = serviceMap.get(request.getClassName());
        }
        if (serviceBean == null) {
            // TODO
        }

        RpcResponse response = new RpcResponse();

        if (System.currentTimeMillis() - request.getCreateMillisTime() > 180000) {
            response.setResult(ReturnT.error("the timestamp difference between admin and executor exceeds the limit."));
            return response;
        }

        try {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

//            FastClass serviceFastClass = FastClass.create(serviceClass);
            Method serviceFastMethod = serviceClass.getMethod(methodName, parameterTypes);

            Object result = serviceFastMethod.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t.getMessage());
        }

        return response;
    }

}
