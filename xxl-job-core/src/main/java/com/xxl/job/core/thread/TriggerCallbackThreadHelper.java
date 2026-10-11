package com.xxl.job.core.thread;

import com.xxl.job.core.constant.Const;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.openapi.admin.AdminBiz;
import com.xxl.job.core.openapi.admin.dto.CallbackData;
import com.xxl.job.core.openapi.admin.dto.CallbackRequest;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.concurrent.MessageQueue;
import com.xxl.tool.core.ArrayTool;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Md5Tool;
import com.xxl.tool.io.FileTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Trigger Callback Thread
 *
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThreadHelper {
    private static final Logger logger = LoggerFactory.getLogger(TriggerCallbackThreadHelper.class);


    /**
     * callback message-queue
     */
    private volatile MessageQueue<CallbackData> callbackMessageQueue;

    /**
     * retry callback-file thread
     */
    private CyclicThread retryCallbackThread;


    /**
     * start
     */
    public void start(final XxlJobExecutor xxlJobExecutor) {

        // valid
        if (xxlJobExecutor.getAdminBizList() == null) {
            logger.warn(">>>>>>>>>>> xxl-job, executor callback config fail, adminAddresses is null.");
            return;
        }


        /**
         * 1、callback message-queue
         */
        callbackMessageQueue = new MessageQueue<CallbackData>(
                "TriggerCallbackThreadHelper#callbackMessageQueue",
                messages -> {

                    // do callback
                    doCallback(messages, xxlJobExecutor);
                },
                1,
                50);

        /**
         * 2、retry callback-file thread
         */
        retryCallbackThread = new CyclicThread("TriggerCallbackThreadHelper#retryCallbackThread", true, new Runnable() {
            @Override
            public void run() {

                // valid empty path
                File callbackLogPath = new File(XxlJobFileAppender.getCallbackLogPath());
                if (!callbackLogPath.exists()) {
                    return;
                }
                // valid file type: must be directory
                if (!FileTool.isDirectory(callbackLogPath)) {
                    FileTool.delete(callbackLogPath);
                    return;
                }
                // valid none file
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
                        List<CallbackData> callbackParamList = GsonTool.fromJsonList(callbackData, CallbackData.class);
                        FileTool.delete(callbackLogFile);

                        // retry callback
                        doCallback(callbackParamList, xxlJobExecutor);
                    } catch (IOException e) {
                        logger.error(">>>>>>>>>>> TriggerCallbackThread retryFailCallbackFile error, callbackLogFile:{}", callbackLogFile.getPath(), e);
                    }
                }

            }
        }, Const.BEAT_TIMEOUT * 1000L, true);
        retryCallbackThread.start();
    }

    /**
     * stop
     */
    public void stop(){
        // 1、stop callbackMessageQueue
        if (callbackMessageQueue != null) {
            callbackMessageQueue.stop();        // attempt wait for callback finish
        }

        // 2、stop retryCallbackThread
        if (callbackMessageQueue != null) {
            retryCallbackThread.stop();
        }
    }


    /**
     * submit callback message
     */
    public void pushCallBack(CallbackData callback){
        if (!callbackMessageQueue.produce(callback)) {
            doCallback(new ArrayList<>(Collections.singletonList(callback)), XxlJobExecutor.getInstance());
        }
        logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
    }


    // ---------------------- do callback ----------------------

    /**
     * do callback, will retry if error
     *
     * @param callbackDataList callback data list
     */
    private void doCallback(List<CallbackData> callbackDataList, final XxlJobExecutor xxlJobExecutor){
        boolean callbackRet = false;

        // callback request, will retry + append-log if fail
        for (AdminBiz adminBiz: xxlJobExecutor.getAdminBizList()) {
            try {
                Response<String> callbackResult = adminBiz.callback(new CallbackRequest(callbackDataList));
                if (callbackResult!=null && callbackResult.isSuccess()) {
                    appendCallbackResult(callbackDataList, "<br>----------- xxl-job job callback finish.");
                    callbackRet = true;
                    break;
                } else {
                    appendCallbackResult(callbackDataList, "<br>----------- xxl-job job callback fail, callbackResult:" + callbackResult);
                }
            } catch (Throwable e) {
                appendCallbackResult(callbackDataList, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }
        }

        // write callback-file, will retry later
        if (!callbackRet) {
            writeCallbackLog(callbackDataList);
        }
    }

    /**
     * append callback result, to each joblog
     */
    private void appendCallbackResult(List<CallbackData> callbackParamList, String logContent){
        for (CallbackData callbackParam: callbackParamList) {
            // determine log file: prefer new merged format, fallback to legacy
            String logFileName;
            if (callbackParam.getJobId() > 0) {
                logFileName = XxlJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTime()), callbackParam.getJobId());
            } else {
                logFileName = XxlJobFileAppender.makeLogFileNameLegacy(new Date(callbackParam.getLogDateTime()), callbackParam.getLogId());
                // for legacy format, only write if file already exists
                if (!new java.io.File(logFileName).exists()) {
                    continue;
                }
            }

            XxlJobContext.setXxlJobContext(new XxlJobContext(
                    -1,
                    null,
                    -1,
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
     * write fail-callback file, will retry later
     *
     * @param callbackParamList callback param list
     */
    private void writeCallbackLog(List<CallbackData> callbackParamList) {
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

}
