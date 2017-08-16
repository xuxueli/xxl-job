package com.xxl.job.core.rpc.netcom.jetty.client;

import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.serialize.HessianSerializer;
import com.xxl.job.core.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jetty client
 * @author xuxueli 2015-11-24 22:25:15
 */
public class JettyClient {
	private static Logger logger = LoggerFactory.getLogger(JettyClient.class);

	public RpcResponse send(RpcRequest request) throws Exception {
		try {
			// serialize request
			byte[] requestBytes = HessianSerializer.serialize(request);

			// reqURL
			String reqURL = request.getServerAddress();
			if (reqURL!=null && reqURL.toLowerCase().indexOf("http")==-1) {
				reqURL = "http://" + request.getServerAddress() + "/";	// IP:PORT, need parse to url
			}

			// remote invoke
			byte[] responseBytes = HttpClientUtil.postRequest(reqURL, requestBytes);
			if (responseBytes == null || responseBytes.length==0) {
				RpcResponse rpcResponse = new RpcResponse();
				rpcResponse.setError("RpcResponse byte[] is null");
				return rpcResponse;
            }

            // deserialize response
			RpcResponse rpcResponse = (RpcResponse) HessianSerializer.deserialize(responseBytes, RpcResponse.class);
			return rpcResponse;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			RpcResponse rpcResponse = new RpcResponse();
			rpcResponse.setError("Client-error:" + e.getMessage());
			return rpcResponse;
		}
	}

}
