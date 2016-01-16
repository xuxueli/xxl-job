package com.xxl.job.client.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.util.HttpUtil;
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
		
		// result
		String _status = HttpUtil.FAIL;
		String _msg = "";
		
		// push data to queue
		String handler_name = _param.get(HandlerRepository.HANDLER_NAME);
		if (handler_name!=null && handler_name.trim().length()>0) {
			HandlerThread handlerThread = handlerTreadMap.get(handler_name);
			if (handlerThread != null) {
				handlerThread.pushData(_param);
				_status = HttpUtil.SUCCESS;
			} else {
				_msg = "handler not found.";
			}
		}else{
			_msg = "param[HANDLER_NAME] not exists.";
		}
		
		
		HashMap<String, String> triggerData = new HashMap<String, String>();
		triggerData.put(HandlerRepository.TRIGGER_LOG_ID, _param.get(HandlerRepository.TRIGGER_LOG_ID));
		triggerData.put(HttpUtil.status, _status);
		triggerData.put(HttpUtil.msg, _msg);
		
		logger.info(">>>>>>>>>>> xxl-job pushHandleQueue end, triggerData:{}", new Object[]{triggerData});
		return JacksonUtil.writeValueAsString(triggerData);
	}
	
}
