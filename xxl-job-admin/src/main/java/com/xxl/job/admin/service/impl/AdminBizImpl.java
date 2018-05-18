package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.annotationtask.enums.ExecutorType;
import com.xxl.job.core.annotationtask.model.ExecutorParam;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.handler.IJobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.xxl.job.core.biz.model.ReturnT.SUCCESS_CODE;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobService xxlJobService;

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==IJobHandler.SUCCESS.getCode()?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
            if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobId())) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {
                        ReturnT<String> triggerChildResult = xxlJobService.triggerJob(childJobId);

                        // add msg
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()== SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        } else if (IJobHandler.FAIL_RETRY.getCode() == handleCallbackParam.getExecuteResult().getCode()){
            ReturnT<String> retryTriggerResult = xxlJobService.triggerJob(log.getJobId());
            callbackMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_exe_fail_retry") +"<<<<<<<<<<< </span><br>";

            callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_msg1"),
                   (retryTriggerResult.getCode()== SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")), retryTriggerResult.getMsg());
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg()!=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        xxlJobLogDao.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        ReturnT returnT=ReturnT.SUCCESS;
        if(registryParam.getXxlJobs()!=null&&registryParam.getXxlJobs().size()>0){//先注册了任务了后才开始 注册执行器
            //1.先查询出这个group所对应的所有注解注册的任务
            String groupName = registryParam.getRegistryKey();
            int groupId = xxlJobGroupDao.loadIdByName(groupName);
            List<XxlJobInfo> jobInfos = xxlJobService.loadByGroupName(groupName);//从数据库查询出来的
            List<XxlJobInfo> xxlJobs = registryParam.getXxlJobs();//从注解读取来的
            xxlJobs.removeAll(jobInfos);//取差集
            returnT =  xxlJobService.addList(xxlJobs,groupId);
        }
        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            xxlJobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return returnT;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        xxlJobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        List<XxlJobRegistry> registries = xxlJobRegistryDao.findByGroupAndRkey(registryParam.getRegistGroup(),registryParam.getRegistryKey());
        if(registries.size()==0){//如果没有执行器了就停掉所有的任务
            List<XxlJobInfo> list = registryParam.getXxlJobs();
            for (XxlJobInfo jobInfo:list){
                int id = xxlJobInfoDao.loadIdByAnnotationIdentity(jobInfo.getAnnotationIdentity());
                xxlJobService.pause(id);
            }
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> triggerJob(int jobId) {
        return xxlJobService.triggerJob(jobId);
    }

    public ReturnT<String> triggerAnnotationJob(ExecutorParam executorParam) {
        String annotationIdentity = (String) executorParam.getParam().get(ExecutorParam.ANNOTATION_IDENTITY);
        int id = xxlJobInfoDao.loadIdByAnnotationIdentity(annotationIdentity);
        if(ExecutorType.PAUSE.equals(executorParam.getExecutorType())){//暂停任务
            xxlJobService.pause(id);
        }else if(ExecutorType.TRIGGER_ONCE.equals(executorParam.getExecutorType())){//触发一次
            return xxlJobService.triggerJob(id,executorParam.getParam());
        }
        return ReturnT.SUCCESS;
    }

}
