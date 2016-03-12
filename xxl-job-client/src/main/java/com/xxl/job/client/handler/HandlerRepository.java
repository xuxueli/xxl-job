package com.xxl.job.client.handler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.client.util.HttpUtil.RemoteCallBack;
import com.xxl.job.client.log.XxlJobFileAppender;
import com.xxl.job.client.util.JacksonUtil;

/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
	private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);
	
	public static final String NAMESPACE = "namespace";
	public enum NameSpaceEnum{RUN, KILL, LOG}
	
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
	public static String service(Map<String, String> _param) {
		logger.info(">>>>>>>>>>> xxl-job service start, _param:{}", new Object[]{_param});
		
		// callback
		RemoteCallBack callback = new RemoteCallBack();
		callback.setStatus(RemoteCallBack.FAIL);

		// check namespace
		String namespace = _param.get(HandlerRepository.NAMESPACE);
		if (namespace==null || namespace.trim().length()==0) {
			callback.setMsg("param[NAMESPACE] can not be null.");
			return JacksonUtil.writeValueAsString(callback);
		}
		
		// parse namespace
		if (namespace.equals(HandlerRepository.NameSpaceEnum.RUN.name())) {
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
			
		} else if (namespace.equals(HandlerRepository.NameSpaceEnum.LOG.name())) {
			String trigger_log_id = _param.get(HandlerRepository.TRIGGER_LOG_ID);
			String trigger_timestamp = _param.get(HandlerRepository.TRIGGER_TIMESTAMP);
			if (trigger_log_id==null || trigger_timestamp==null) {
				callback.setMsg("trigger_log_id | trigger_timestamp can not be null.");
				return JacksonUtil.writeValueAsString(callback);
			}
			int logId = -1;
			Date triggerDate = null;
			try {
				logId = Integer.valueOf(trigger_log_id);
				triggerDate = new Date(Long.valueOf(trigger_timestamp));
			} catch (Exception e) {
			}
			if (logId<=0 || triggerDate==null) {
				callback.setMsg("trigger_log_id | trigger_timestamp is not parsed valid.");
				return JacksonUtil.writeValueAsString(callback);
			}
			String logConteng = XxlJobFileAppender.readLog(triggerDate, trigger_log_id);
			callback.setStatus(RemoteCallBack.SUCCESS);
			callback.setMsg(logConteng);
		}
		
		logger.info(">>>>>>>>>>> xxl-job service end, triggerData:{}", new Object[]{callback});
		return JacksonUtil.writeValueAsString(callback); 
	}
	
}
