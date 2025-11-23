package com.xxl.job.core.thread;

import com.xxl.job.core.openapi.AdminBiz;
import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.tool.core.ArrayTool;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.encrypt.Md5Tool;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.io.FileTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Trigger Callback Thread
 *
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
    private static final Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static final TriggerCallbackThread instance = new TriggerCallbackThread();
    public static TriggerCallbackThread getInstance(){
        return instance;
    }

    /**
     * job results callback queue
     */
    private final LinkedBlockingQueue<HandleCallbackRequest> callBackQueue = new LinkedBlockingQueue<>();
    public static void pushCallBack(HandleCallbackRequest callback){
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
    }

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    private volatile boolean toStop = false;
    public void start() {

        // valid
        if (XxlJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> xxl-job, executor callback config fail, adminAddresses is null.");
            return;
        }

        /**
         * trigger callback thread
         */
        triggerCallbackThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // normal callback
                while(!toStop){
                    try {
                        HandleCallbackRequest callback = getInstance().callBackQueue.take();
                        if (callback != null) {

                            // collect callback data
                            List<HandleCallbackRequest> callbackParamList = new ArrayList<>();
                            callbackParamList.add(callback);                                            // add one element
                            int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);    // drainTo other all elements

                            // do callback, will retry if error
                            if (CollectionTool.isNotEmpty(callbackParamList)) {
                                doCallback(callbackParamList);
                            }
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                // thead stop, callback lasttime
                try {
                    // collect callback data
                    List<HandleCallbackRequest> callbackParamList = new ArrayList<>();
                    int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);

                    // do callback
                    if (CollectionTool.isNotEmpty(callbackParamList)) {
                        doCallback(callbackParamList);
                    }
                } catch (Throwable e) {
                    if (!toStop) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, executor callback thread destroy.");

            }
        });
        triggerCallbackThread.setDaemon(true);
        triggerCallbackThread.setName("xxl-job, executor TriggerCallbackThread");
        triggerCallbackThread.start();


        /**
         * callback fail retry thread
         */
        triggerRetryCallbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!toStop){
                    try {
                        retryFailCallbackFile();
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }

                    }
                    try {
                        TimeUnit.SECONDS.sleep(Const.BEAT_TIMEOUT);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, executor retry callback thread destroy.");
            }
        });
        triggerRetryCallbackThread.setDaemon(true);
        triggerRetryCallbackThread.setName("xxl-job, executor TriggerRetryCallbackThread");
        triggerRetryCallbackThread.start();

    }
    public void toStop(){
        toStop = true;
        // stop callback, interrupt and wait
        if (triggerCallbackThread != null) {    // support empty admin address
            triggerCallbackThread.interrupt();
            try {
                triggerCallbackThread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

        // stop retry, interrupt and wait
        if (triggerRetryCallbackThread != null) {
            triggerRetryCallbackThread.interrupt();
            try {
                triggerRetryCallbackThread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * do callback, will retry if error
     *
     * @param callbackParamList callback param list
     */
    private void doCallback(List<HandleCallbackRequest> callbackParamList){
        boolean callbackRet = false;
        // callback, will retry if error
        for (AdminBiz adminBiz: XxlJobExecutor.getAdminBizList()) {
            try {
                Response<String> callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult!=null && callbackResult.isSuccess()) {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback finish.");
                    callbackRet = true;
                    break;
                } else {
                    callbackLog(callbackParamList, "<br>----------- xxl-job job callback fail, callbackResult:" + callbackResult);
                }
            } catch (Throwable e) {
                callbackLog(callbackParamList, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }
        }
        if (!callbackRet) {
            appendFailCallbackFile(callbackParamList);
        }
    }

    /**
     * callback log
     */
    private void callbackLog(List<HandleCallbackRequest> callbackParamList, String logContent){
        for (HandleCallbackRequest callbackParam: callbackParamList) {
            String logFileName = XxlJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            XxlJobContext.setXxlJobContext(new XxlJobContext(
                    -1,
                    null,
                    -1,
                    logFileName,
                    -1,
                    -1));
            XxlJobHelper.log(logContent);
        }
    }


    // ---------------------- fail-callback file ----------------------

    /**
     * fail-callback file name
     */
    private static final String failCallbackFileName = XxlJobFileAppender
            .getCallbackLogPath()
            .concat(File.separator)
            .concat("xxl-job-callback-{x}")
            .concat(".log");

    /**
     * append fail-callback file
     *
     * @param callbackParamList callback param list
     */
    private void appendFailCallbackFile(List<HandleCallbackRequest> callbackParamList) {
        // valid
        if (CollectionTool.isEmpty(callbackParamList)) {
            return;
        }

        // generate callback data
        String callbackData = GsonTool.toJson(callbackParamList);
        String callbackDataMd5 = Md5Tool.md5(callbackData);


        // create file
        String finalLogFileName = failCallbackFileName.replace("{x}", callbackDataMd5);

        // write callback log
        try {
            FileTool.writeString(finalLogFileName, callbackData);
        } catch (IOException e) {
            logger.error(">>>>>>>>>>> TriggerCallbackThread appendFailCallbackFile error, finalLogFileName:{}", finalLogFileName, e);
        }
    }

    /**
     * retry fail-callback file
     */
    private void retryFailCallbackFile() {

        // valid
        File callbackLogPath = new File(XxlJobFileAppender.getCallbackLogPath());
        if (!callbackLogPath.exists()) {
            return;
        }
        // valid file type: must be directory
        if (!FileTool.isDirectory(callbackLogPath)) {
            FileTool.delete(callbackLogPath);
            return;
        }
        // valid file in path: pass if empty
        if (ArrayTool.isEmpty(callbackLogPath.listFiles())) {
            return;
        }

        // load and clear file, do retry
        for (File callbackLogFile: callbackLogPath.listFiles()) {
            try {
                // load data
                String callbackData = FileTool.readString(callbackLogFile.getPath());
                if (StringTool.isBlank(callbackData)) {
                    FileTool.delete(callbackLogFile);
                    continue;
                }

                // parse callback param
                List<HandleCallbackRequest> callbackParamList = GsonTool.fromJsonList(callbackData, HandleCallbackRequest.class);
                FileTool.delete(callbackLogFile);

                // retry callback
                doCallback(callbackParamList);
            } catch (IOException e) {
                logger.error(">>>>>>>>>>> TriggerCallbackThread retryFailCallbackFile error, callbackLogFile:{}", callbackLogFile.getPath(), e);
            }
        }

    }

}
