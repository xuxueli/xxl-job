package com.xxl.job.core.rpc.netcom.jetty.server;

import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.rpc.serialize.HessianSerializer;
import com.xxl.job.core.util.HttpClientUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * jetty handler
 * @author xuxueli 2015-11-19 22:32:36
 */
public class JettyServerHandler extends AbstractHandler {


	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// deserialize request
		byte[] requestBytes = HttpClientUtil.readBytes(request);
		RpcRequest rpcRequest = (RpcRequest) HessianSerializer.deserialize(requestBytes, RpcRequest.class);
		
		// invoke
        RpcResponse rpcResponse = NetComServerFactory.invokeService(rpcRequest, null);

        // serialize response
        byte[] responseBytes = HessianSerializer.serialize(rpcResponse);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();
		
	}

}
