package com.xxl.job.core.thread;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();
    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        HandleCallbackParam callback = callBackQueue.take();
                        if (callback != null) {
                            for (String address : callback.getLogAddress()) {
                                try {
                                    // callback
                                    AdminBiz adminBiz = (AdminBiz) new NetComClientProxy(AdminBiz.class, address).getObject();
                                    ReturnT<String> callbackResult = adminBiz.callback(callback);

                                    logger.info(">>>>>>>>>>> xxl-job callback , CallbackParam:{}, callbackResult:{}", new Object[]{callback.toString(), callbackResult.toString()});
                                    if (ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
                                        break;
                                    }
                                } catch (Exception e) {
                                    logger.error(">>>>>>>>>>> xxl-job TriggerCallbackThread Exception:", e);
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
    public static void pushCallBack(HandleCallbackParam callback){
        callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
    }

}
