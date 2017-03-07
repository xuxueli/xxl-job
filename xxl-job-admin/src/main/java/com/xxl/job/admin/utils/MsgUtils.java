package com.xxl.job.admin.utils;

import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * @Author bruce.ge
 * @Date 2017/3/7
 * @Description
 */
public class MsgUtils {

    public static void whenTriggerMsgOverSize(XxlJobLog jobLog) {
        if (jobLog.getTriggerMsg()!=null && jobLog.getTriggerMsg().length()>2000) {
            jobLog.setTriggerMsg(jobLog.getTriggerMsg().substring(0, 2000));
        }
    }


    public static void whenHandleMsgOverSize(XxlJobLog jobLog) {
        if (jobLog.getHandleMsg()!=null && jobLog.getHandleMsg().length()>2000) {
            jobLog.setHandleMsg(jobLog.getHandleMsg().substring(0, 2000));
        }
    }
}
