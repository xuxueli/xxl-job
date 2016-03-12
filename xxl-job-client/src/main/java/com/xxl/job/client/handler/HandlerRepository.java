package com.xxl.job.client.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.util.HttpUtil.RemoteCallBack;
import com.xxl.job.client.util.JacksonUtil;

/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
	private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);
	
	public static final String HANDLER_ADDRESS = "handler_address";
	public static final String HANDLER_NAME = "handler_name";
	public static final String HANDLER_PARAMS = "handler_params";
	
	public static final String TRIGGER_LOG_ID = "trigger_log_id";
	public static final String TRIGGER_LOG_URL = "trigger_log_url";
	public static final String TRIGGER_TIMESTAMP = "trigger_timestamp";
	
	public static ConcurrentHashMap<String, HandlerThread> handlerTreadMap = new ConcurrentHashMap<String, HandlerThread>();
	
	// regist handler
	public static void regist(String handleName, IJobHandler handler){
		HandlerThread handlerThread = new HandlerThread(handler);
		handlerThread.start();
		handlerTreadMap.put(handleName, handlerThread);	// putIfAbsent
		logger.info(">>>>>>>>>>> xxl-job regist handler success, handleName:{}, handler:{}", new Object[]{handleName, handler});
	}
	
	// handler push to queue
	public static String pushHandleQueue(Map<String, String> _param) {
		logger.info(">>>>>>>>>>> xxl-job pushHandleQueue start, _param:{}", new Object[]{_param});
		
		// callback
		RemoteCallBack callback = new RemoteCallBack();
		callback.setStatus(RemoteCallBack.FAIL);
		
		// encryption check
		long timestamp = _param.get(HandlerRepository.TRIGGER_TIMESTAMP)!=null?Long.valueOf(_param.get(HandlerRepository.TRIGGER_TIMESTAMP)):-1;
		if (System.currentTimeMillis() - timestamp > 60000) {
			callback.setMsg("Timestamp check failed.");
			return JacksonUtil.writeValueAsString(callback);
		}
				
		// push data to queue
		String handler_name = _param.get(HandlerRepository.HANDLER_NAME);
		if (handler_name!=null && handler_name.trim().length()>0) {
			HandlerThread handlerThread = handlerTreadMap.get(handler_name);
			if (handlerThread != null) {
				handlerThread.pushData(_param);
				callback.setStatus(RemoteCallBack.SUCCESS);
			} else {
				callback.setMsg("handler[" + handler_name + "] not found.");
			}
		}else{
			callback.setMsg("param[HANDLER_NAME] can not be null.");
		}
		
		logger.info(">>>>>>>>>>> xxl-job pushHandleQueue end, triggerData:{}", new Object[]{callback});
		return JacksonUtil.writeValueAsString(callback);
	}
	
}
