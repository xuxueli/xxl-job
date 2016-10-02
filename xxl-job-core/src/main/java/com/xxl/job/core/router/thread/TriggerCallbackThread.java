package com.xxl.job.core.router.thread;

import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.util.XxlJobNetCommUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static LinkedBlockingQueue<RequestModel> callBackQueue = new LinkedBlockingQueue<RequestModel>();
    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        RequestModel callback = callBackQueue.take();
                        if (callback != null) {
                            for (String address : callback.getLogAddress()) {
                                try {
                                    ResponseModel responseModel = XxlJobNetCommUtil.postHex(XxlJobNetCommUtil.addressToUrl(address), callback);
                                    logger.info(">>>>>>>>>>> xxl-job callback , RequestModel:{}, ResponseModel:{}", new Object[]{callback.toString(), responseModel.toString()});
                                    if (ResponseModel.SUCCESS.equals(responseModel.getStatus())) {
                                        break;
                                    }
                                } catch (Exception e) {
                                    logger.info("JobThread Exception:", e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        }).start();
    }
    public static void pushCallBack(RequestModel callback){
        callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
    }

}
