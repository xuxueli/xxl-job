package com.xxl.job.core.rpc.netcom.jetty.server;

import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.serialize.HessianSerializer;
import com.xxl.job.core.util.RpcUtil;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jetty handler
 * @author xuxueli 2015-11-19 22:32:36
 */
public class JettyServerHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// invoke
        RpcResponse rpcResponse = doInvoke(request);

        // serialize response
        byte[] responseBytes = HessianSerializer.serialize(rpcResponse);

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();

	}

	private RpcResponse doInvoke(HttpServletRequest request) {
		return RpcUtil.getRpcResponse(request);
	}
}
