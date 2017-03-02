package com.xxl.job.admin.core.biz;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.schedule.DynamicSchedulerUtil;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Override
    public ReturnT<String> callback(TriggerParam triggerParam) {

        // valid log item
        XxlJobLog log = DynamicSchedulerUtil.xxlJobLogDao.load(triggerParam.getLogId());
        if (log == null) {
            return new ReturnT(ReturnT.FAIL_CODE, "log item not found.");
        }

        // trigger success, to trigger child job, and avoid repeat trigger child job
        String childTriggerMsg = null;
        if ((ReturnT.SUCCESS_CODE+"").equals(triggerParam.getStatus()) && !(ReturnT.SUCCESS_CODE+"").equals(log.getHandleStatus())) {
            XxlJobInfo xxlJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
            if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobKey())) {
                childTriggerMsg = "<hr>";
                String[] childJobKeys = xxlJobInfo.getChildJobKey().split(",");
                for (int i = 0; i < childJobKeys.length; i++) {
                    String[] jobKeyArr = childJobKeys[i].split("_");
                    if (jobKeyArr!=null && jobKeyArr.length==2) {
                        XxlJobInfo childJobInfo = DynamicSchedulerUtil.xxlJobInfoDao.load(Integer.valueOf(jobKeyArr[0]), jobKeyArr[1]);
                        if (childJobInfo!=null) {
                            try {
                                boolean ret = DynamicSchedulerUtil.triggerJob(childJobInfo.getJobName(), String.valueOf(childJobInfo.getJobGroup()));

                                // add msg
                                childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务成功, 子任务Key: {2}, status: {3}, 子任务描述: {4}",
                                        (i+1), childJobKeys.length, childJobKeys[i], ret, childJobInfo.getJobDesc());
                            } catch (SchedulerException e) {
                                logger.error("", e);
                            }
                        } else {
                            childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务失败, 子任务xxlJobInfo不存在, 子任务Key: {2}",
                                    (i+1), childJobKeys.length, childJobKeys[i]);
                        }
                    } else {
                        childTriggerMsg += MessageFormat.format("<br> {0}/{1} 触发子任务失败, 子任务Key格式错误, 子任务Key: {2}",
                                (i+1), childJobKeys.length, childJobKeys[i]);
                    }
                }

            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (triggerParam.getMsg() != null) {
            handleMsg.append("执行备注：").append(triggerParam.getMsg());
        }
        if (childTriggerMsg !=null) {
            handleMsg.append("<br>子任务触发备注：").append(childTriggerMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleStatus(triggerParam.getStatus());
        log.setHandleMsg(handleMsg.toString());
        DynamicSchedulerUtil.xxlJobLogDao.updateHandleInfo(log);

        return new ReturnT(ReturnT.SUCCESS_CODE, null);
    }

}
