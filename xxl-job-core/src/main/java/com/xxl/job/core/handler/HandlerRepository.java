package com.xxl.job.core.handler;

import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;
import com.xxl.job.core.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * handler repository
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
	private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);
	
	public enum HandlerParamEnum{
		/**
		 * trigger timestamp
		 */
		TIMESTAMP,
		/**
		 * trigger action
		 */
		ACTION,
		/**
		 * job group
		 */
		JOB_GROUP,
		/**
		 * job name
		 */
		JOB_NAME,
		/**
		 * params of jobhandler
		 */
		EXECUTOR_PARAMS,
		/**
		 * switch of glue job: 0-no，1-yes
		 */
		GLUE_SWITCH,
		/**
		 * address for callback log
		 */
		LOG_ADDRESS,
		/**
		 * log id
		 */
		LOG_ID,
		/**
		 * log date
		 */
		LOG_DATE
	}
	public enum ActionEnum{RUN, KILL, LOG, BEAT}
	
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
		logger.debug(">>>>>>>>>>> xxl-job service start, _param:{}", new Object[]{_param});
		
		// callback
		RemoteCallBack callback = new RemoteCallBack();
		callback.setStatus(RemoteCallBack.FAIL);

		// check namespace
		String namespace = _param.get(HandlerParamEnum.ACTION.name());
		if (namespace==null || namespace.trim().length()==0) {
			callback.setMsg("param[NAMESPACE] can not be null.");
			return JacksonUtil.writeValueAsString(callback);
		}
		// encryption check
		long timestamp = _param.get(HandlerParamEnum.TIMESTAMP.name())!=null?Long.valueOf(_param.get(HandlerParamEnum.TIMESTAMP.name())):-1;
		if (System.currentTimeMillis() - timestamp > 60000) {
			callback.setMsg("Timestamp check failed.");
			return JacksonUtil.writeValueAsString(callback);
		}
					
		// parse namespace
		if (namespace.equals(ActionEnum.RUN.name())) {

			// generate jobKey
			String job_group = _param.get(HandlerParamEnum.JOB_GROUP.name());
			String job_name = _param.get(HandlerParamEnum.JOB_NAME.name());
			if (job_group == null || job_group.trim().length()==0 || job_name == null || job_name.trim().length()==0) {
				callback.setMsg("JOB_GROUP or JOB_NAME is null.");
				return JacksonUtil.writeValueAsString(callback);
			}
			String jobKey = job_group.concat("_").concat(job_name);

			// glue switch
			String handler_glue_switch = _param.get(HandlerParamEnum.GLUE_SWITCH.name());
			if (handler_glue_switch==null || handler_glue_switch.trim().length()==0){
				callback.setMsg("GLUE_SWITCH is null.");
				return JacksonUtil.writeValueAsString(callback);
			}

			HandlerThread handlerThread = handlerTreadMap.get(jobKey);;
			if ("0".equals(handler_glue_switch)) {
				// bean model
				if (handlerThread == null) {
					callback.setMsg("handler for jobKey=[" + jobKey + "] not found.");
					return JacksonUtil.writeValueAsString(callback);
				}
			} else {
				// glue
				if (handlerThread==null) {
					HandlerRepository.regist(jobKey, new GlueJobHandler(job_group, job_name));
				}
				handlerThread = handlerTreadMap.get(jobKey);
			}

			// push data to queue
			handlerThread.pushData(_param);
			callback.setStatus(RemoteCallBack.SUCCESS);
		} else if (namespace.equals(ActionEnum.LOG.name())) {
			String log_id = _param.get(HandlerParamEnum.LOG_ID.name());
			String log_date = _param.get(HandlerParamEnum.LOG_DATE.name());
			if (log_id==null || log_date==null) {
				callback.setMsg("LOG_ID | LOG_DATE can not be null.");
				return JacksonUtil.writeValueAsString(callback);
			}
			int logId = -1;
			Date triggerDate = null;
			try {
				logId = Integer.valueOf(log_id);
				triggerDate = new Date(Long.valueOf(log_date));
			} catch (Exception e) {
			}
			if (logId<=0 || triggerDate==null) {
				callback.setMsg("LOG_ID | LOG_DATE parse error.");
				return JacksonUtil.writeValueAsString(callback);
			}
			String logConteng = XxlJobFileAppender.readLog(triggerDate, log_id);
			callback.setStatus(RemoteCallBack.SUCCESS);
			callback.setMsg(logConteng);
		} else if (namespace.equals(ActionEnum.KILL.name())) {
			// generate jobKey
			String job_group = _param.get(HandlerParamEnum.JOB_GROUP.name());
			String job_name = _param.get(HandlerParamEnum.JOB_NAME.name());
			if (job_group == null || job_group.trim().length()==0 || job_name == null || job_name.trim().length()==0) {
				callback.setMsg("JOB_GROUP or JOB_NAME is null.");
				return JacksonUtil.writeValueAsString(callback);
			}
			String jobKey = job_group.concat("_").concat(job_name);

			// kill handlerThread, and create new one
			HandlerThread handlerThread = handlerTreadMap.get(jobKey);
			if (handlerThread != null) {
				IJobHandler handler = handlerThread.getHandler();
				handlerThread.toStop();
				handlerThread.interrupt();
				regist(jobKey, handler);
				callback.setStatus(RemoteCallBack.SUCCESS);
			} else {
				callback.setMsg("handler for jobKey=[" + jobKey + "] not found.");
			}
				
		} else if (namespace.equals(ActionEnum.BEAT.name())) {
			callback.setStatus(RemoteCallBack.SUCCESS);
			callback.setMsg(null);
		} else {
			callback.setMsg("param[Action] is not valid.");
			return JacksonUtil.writeValueAsString(callback);
		}
		
		logger.debug(">>>>>>>>>>> xxl-job service end, triggerData:{}");
		return JacksonUtil.writeValueAsString(callback); 
	}
	
	// ----------------------- for callback log -----------------------
	private static LinkedBlockingQueue<HashMap<String, String>> callBackQueue = new LinkedBlockingQueue<HashMap<String, String>>();
	static {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						HashMap<String, String> item = callBackQueue.take();
						if (item != null) {
							RemoteCallBack callback = null;
							try {
								callback = HttpUtil.post(item.get("_address"), item);
							} catch (Exception e) {
								logger.info("HandlerThread Exception:", e);
							}
							logger.info(">>>>>>>>>>> xxl-job callback , params:{}, result:{}", new Object[]{item, callback});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	public static void pushCallBack(String address, HashMap<String, String> params){
		params.put("_address", address);
		callBackQueue.add(params);
	}
	
}
