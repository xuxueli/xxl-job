package com.xxl.job.executor.factory.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.executor.context.XxlJobContext;
import com.xxl.job.executor.context.XxlJobHelper;
import com.xxl.job.executor.factory.repository.XxlJobRepository;
import com.xxl.job.executor.service.AdminClient;
import com.xxl.job.executor.utils.JobLogUtils;
import com.xxl.job.spring.boot.autoconfigure.XxlJobExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 触发回调线程
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class TriggerCallbackThread extends BaseTaskThread {

    private static final String failCallbackFilePath = JobLogUtils.getLogPath().concat(File.separator).concat("callback-log").concat(File.separator);
    private static final String failCallbackFileName = failCallbackFilePath.concat("xxl-job-callback-{x}").concat(".log");

    /**
     * callback thread
     */
    private Thread triggerCallbackThread;
    private Thread triggerRetryCallbackThread;
    private volatile boolean toStop = false;

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private XxlJobExecutorProperties xxlJobExecutorProperties;

    @Autowired
    private XxlJobRepository xxlJobRepository;

    @Override
    public void start() {

        // callback
        triggerCallbackThread = newThread(() -> {
            // normal callback
            while (!toStop) {
                try {
                    HandleCallbackParam callback = xxlJobRepository.takeCallBack();
                    if (ObjectUtil.isNotNull(callback)) {

                        // callback list param
                        List<HandleCallbackParam> callbackParams = xxlJobRepository.drainToCallBack();
                        callbackParams.add(callback);

                        // callback, will retry if error
                        if (CollectionUtil.isNotEmpty(callbackParams)) {
                            doCallback(callbackParams);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            // last callback
            try {
                List<HandleCallbackParam> callbackParams = xxlJobRepository.drainToCallBack();
                if (CollectionUtil.isNotEmpty(callbackParams)) {
                    doCallback(callbackParams);
                }
            } catch (Exception e) {
                if (!toStop) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info(">>>>>>>>>>> xxl-job, executor callback thread destroy.");
        }, "xxl-job, executor TriggerCallbackThread");

        // retry
        triggerRetryCallbackThread = newThread(() -> {
            while (!toStop) {
                try {
                    retryFailCallbackFile();
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }

                }
                try {
                    TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT.getValue());
                } catch (InterruptedException e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info(">>>>>>>>>>> xxl-job, executor retry callback thread destroy.");
        }, "xxl-job, executor triggerRetryCallbackThread");

    }

    @Override
    public void stop() {
        toStop = true;
        // stop callback, interrupt and wait
        ThreadUtil.interrupt(triggerCallbackThread, Boolean.TRUE);

        // stop retry, interrupt and wait
        ThreadUtil.interrupt(triggerRetryCallbackThread, Boolean.TRUE);
    }

    /**
     * 执行回调
     *
     * @param callbackParams 回调参数
     */
    private void doCallback(List<HandleCallbackParam> callbackParams) {
        boolean callbackRet = false;
        // callback, will retry if error
        List<String> addresses = xxlJobExecutorProperties.getAdmin().getAddresses();
        for (String address : addresses) {
            try {
                adminClient.callback(address, callbackParams);
                callbackLog(callbackParams, "<br>----------- xxl-job job callback finish.");
                callbackRet = true;
            } catch (Exception e) {
                callbackLog(callbackParams, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }

            if (!callbackRet) {
                appendFailCallbackFile(callbackParams);
            }
        }
    }



    /**
     * callback log
     */
    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            String logFileName = JobLogUtils.makeLogFileName(new Date(callbackParam.getLogDateTime()), callbackParam.getLogId());
            XxlJobContext.setJobContext(new XxlJobContext(
                    -1,
                    null,
                    logFileName,
                    -1,
                    -1));
            XxlJobHelper.log(logContent);
        }
    }

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParams) {
        if (CollectionUtil.isNotEmpty(callbackParams)) return;

        byte[] bytes = JSON.toJSONBytes(callbackParams);

        File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
        if (callbackLogFile.exists()) {
            for (int i = 0; i < 100; i++) {
                callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i))));
                if (!callbackLogFile.exists()) {
                    break;
                }
            }

        }
        FileUtil.mkParentDirs(callbackLogFile);
        FileUtil.writeBytes(bytes, callbackLogFile);
    }

    private void retryFailCallbackFile() {

        // valid
        File callbackLogPath = new File(failCallbackFilePath);
        if (!callbackLogPath.exists()) return;

        if (callbackLogPath.isFile()) callbackLogPath.delete();
        if (!callbackLogPath.isDirectory() || ArrayUtil.isEmpty(callbackLogPath.list())) return;

        // load and clear file, retry
        for (File callbackLogFile : callbackLogPath.listFiles()) {
            byte[] bytes = FileUtil.readBytes(callbackLogFile);

            // avoid empty file
            if (ArrayUtil.isEmpty(bytes)) {
                callbackLogFile.delete();
                continue;
            }

            List<HandleCallbackParam> callbackParamList = JSON.parseObject(bytes, List.class);
            callbackLogFile.delete();
            doCallback(callbackParamList);
        }

    }

}
