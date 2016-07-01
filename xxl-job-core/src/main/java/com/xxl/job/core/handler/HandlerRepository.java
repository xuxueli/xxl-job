package com.xxl.job.core.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.handler.impl.HttpJobHandler;
import com.xxl.job.core.handler.impl.PythonJobHandler;
import com.xxl.job.core.handler.impl.ShellJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;
import com.xxl.job.core.util.JacksonUtil;

/**
 * handler repository
 * 
 * @author xuxueli 2015-12-19 19:28:44
 */
public class HandlerRepository {
    private static Logger logger = LoggerFactory.getLogger(HandlerRepository.class);

    public enum HandlerParamEnum {
        /**
         * trigger timestamp
         */
        TIMESTAMP,
        /**
         * trigger action
         */
        ACTION,
        /**
         * remote executor jobhandler
         */
        EXECUTOR_HANDLER,
        /**
         * params of jobhandler
         */
        EXECUTOR_PARAMS,
        /**
         * switch of job, if open glue model
         */
        JOB_TYPE,
        /**
         * job group
         */
        JOB_GROUP,
        /**
         * job name
         */
        JOB_NAME,
        /**
         * job path
         */
        JOB_PATH,
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

    public enum ActionEnum {
        RUN,
        KILL,
        LOG,
        BEAT
    }

    public static ConcurrentHashMap<String, HandlerThread> handlerTreadMap = new ConcurrentHashMap<String, HandlerThread>();

    // regist handler
    public static void regist(String handleName, IJobHandler handler) {
        HandlerThread handlerThread = new HandlerThread(handler);
        handlerThread.start();
        handlerTreadMap.put(handleName, handlerThread); // putIfAbsent
        logger.info(">>>>>>>>>>> xxl-job regist handler success, handleName:{}, handler:{}",
                new Object[] { handleName, handler });
    }

    // handler push to queue
    public static String service(Map<String, String> _param) {
        logger.debug(">>>>>>>>>>> xxl-job service start, _param:{}", new Object[] { _param });

        // callback
        RemoteCallBack callback = new RemoteCallBack();
        callback.setStatus(RemoteCallBack.FAIL);

        // check namespace
        String namespace = _param.get(HandlerParamEnum.ACTION.name());
        if (namespace == null || namespace.trim().length() == 0) {
            callback.setMsg("param[NAMESPACE] can not be null.");
            return JacksonUtil.writeValueAsString(callback);
        }
        // encryption check
        long timestamp = _param.get(HandlerParamEnum.TIMESTAMP.name()) != null
                ? Long.valueOf(_param.get(HandlerParamEnum.TIMESTAMP.name())) : -1;
        if (System.currentTimeMillis() - timestamp > 60000) {
            callback.setMsg("Timestamp check failed.");
            return JacksonUtil.writeValueAsString(callback);
        }

        // parse namespace
        if (namespace.equals(ActionEnum.RUN.name())) {
            // push data to queue
            String jobType = _param.get(HandlerParamEnum.JOB_TYPE.name());
            HandlerThread handlerThread = getHandlerThread(jobType, _param);
            if (handlerThread == null) {
                callback.setMsg("bean model handler[HANDLER_NAME] not found.");
                return JacksonUtil.writeValueAsString(callback);

            }
            handlerThread.pushData(_param);
            callback.setStatus(RemoteCallBack.SUCCESS);
        } else if (namespace.equals(ActionEnum.LOG.name())) {
            String log_id = _param.get(HandlerParamEnum.LOG_ID.name());
            String log_date = _param.get(HandlerParamEnum.LOG_DATE.name());
            if (log_id == null || log_date == null) {
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
            if (logId <= 0 || triggerDate == null) {
                callback.setMsg("LOG_ID | LOG_DATE parse error.");
                return JacksonUtil.writeValueAsString(callback);
            }
            String logConteng = XxlJobFileAppender.readLog(triggerDate, log_id);
            callback.setStatus(RemoteCallBack.SUCCESS);
            callback.setMsg(logConteng);
        } else if (namespace.equals(ActionEnum.KILL.name())) {
            // kill handlerThread, and create new one
            String handlerName = null;
            String executor_handler = _param.get(HandlerParamEnum.EXECUTOR_HANDLER.name());
            String[] handerStrs = executor_handler.replace("，", ",").split(",");
            if (handerStrs == null || handerStrs.length != 2) {
                callback.setMsg("bean job , param[EXECUTOR_HANDLER] is null");
                return JacksonUtil.writeValueAsString(callback);
            }
            handlerName = handerStrs[1];
            HandlerThread handlerThread = handlerTreadMap.get(handlerName);
            if (handlerThread != null) {
                IJobHandler handler = handlerThread.getHandler();
                handlerThread.toStop();
                handlerThread.interrupt();
                regist(handlerName, handler);
                callback.setStatus(RemoteCallBack.SUCCESS);
            } else {
                callback.setMsg("job handler[" + handlerName + "] not found.");
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

    private static HandlerThread getHandlerThread(String jobType, Map<String, String> _param) {
        HandlerThread handlerThread = null;
        String job_group = _param.get(HandlerParamEnum.JOB_GROUP.name());
        String job_name = _param.get(HandlerParamEnum.JOB_NAME.name());
        if (job_group == null || job_group.trim().length() == 0 || job_name == null
                || job_name.trim().length() == 0) {
            return null;
        }

        if ("2".equals(jobType)) {
            String jobPath = _param.get(HandlerParamEnum.JOB_PATH.name());
            handlerThread = handlerTreadMap.get(jobPath);
            if (handlerThread == null) {
                HandlerRepository.regist(jobPath, new HttpJobHandler(job_group, job_name, jobPath));
            }
            handlerThread = handlerTreadMap.get(jobPath);
            return handlerThread;
        }

        if ("3".equals(jobType)) {
            String jobPath = _param.get(HandlerParamEnum.JOB_PATH.name());
            handlerThread = handlerTreadMap.get(jobPath);
            if (handlerThread == null) {
                HandlerRepository.regist(jobPath,
                        new PythonJobHandler(job_group, job_name, jobPath));
            }
            handlerThread = handlerTreadMap.get(jobPath);
            return handlerThread;
        }

        if ("4".equals(jobType)) {
            String jobPath = _param.get(HandlerParamEnum.JOB_PATH.name());
            handlerThread = handlerTreadMap.get(jobPath);
            if (handlerThread == null) {
                HandlerRepository.regist(jobPath,
                        new ShellJobHandler(job_group, job_name, jobPath));
            }
            handlerThread = handlerTreadMap.get(jobPath);
            return handlerThread;
        }
        return null;
    }

    // ----------------------- for callback log -----------------------
    private static LinkedBlockingQueue<HashMap<String, String>> callBackQueue = new LinkedBlockingQueue<HashMap<String, String>>();
    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        HashMap<String, String> item = callBackQueue.poll();
                        if (item != null) {
                            RemoteCallBack callback = null;
                            try {
                                callback = HttpUtil.post(item.get("_address"), item);
                            } catch (Exception e) {
                                logger.info("HandlerThread Exception:", e);
                            }
                            logger.info(">>>>>>>>>>> xxl-job callback , params:{}, result:{}",
                                    new Object[] { item, callback });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void pushCallBack(String address, HashMap<String, String> params) {
        params.put("_address", address);
        callBackQueue.add(params);
    }

}
