package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.HandleCodeEnum;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

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


    /**
     * 新增任务
     * @param jobInfo
     * @return
     */
    public ReturnT<String> addJob(XxlJobInfo jobInfo){
        return xxlJobService.add(jobInfo);
    }


    /**
     * 批量新增，主要用于新增某个父任务下的子任务
     * @param toAdds
     * @return
     */
    public ReturnT<String> addChildJobs(List<XxlJobInfo> toAdds){
        Set<Integer> parentIds=new HashSet<>();
        for(XxlJobInfo jobInfo:toAdds){
            if(jobInfo.getParentId()!=null && jobInfo.getParentId()!=0){
                int size=xxlJobInfoDao.query(jobInfo.getParentId(),null,jobInfo.getExecutorParam()).size();
                if(size>0){
                    logger.info(String.format("已经存在，跳过[%s,%s]",jobInfo.getParentId(),jobInfo.getExecutorParam()));
                    continue;
                }
                parentIds.add(jobInfo.getParentId());
            }
            xxlJobService.add(jobInfo);
        }
        for(Integer id:parentIds){
            xxlJobService.updateChildIds(id);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 批量删除，主要用于删除某个父任务下的子任务
     * @param toDeletes
     * @return
     */
    public ReturnT<String> deleteChildJobs(List<XxlJobInfo> toDeletes){
        Set<Integer> parentIds=new HashSet<>();
        for(XxlJobInfo jobInfo:toDeletes){
            XxlJobInfo pre=xxlJobInfoDao.loadById(jobInfo.getId());
            if(pre==null){
                logger.info(String.format("删除数据不存在，跳过[%s,%s,%s]",jobInfo.getId(),jobInfo.getParentId(),jobInfo.getExecutorParam()));
                continue;
            }
            xxlJobService.remove(jobInfo.getId());
            if(jobInfo.getParentId()!=null && jobInfo.getParentId()>0){
                parentIds.add(jobInfo.getParentId());
            }
        }
        for(Integer id:parentIds){
            xxlJobService.updateChildIds(id);
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 批量更新
     * @param jobInfos
     * @return
     */
    public ReturnT<String> updateJobs(List<XxlJobInfo> jobInfos){
        for(XxlJobInfo jobInfo:jobInfos){
            xxlJobService.update(jobInfo);
        }
        return ReturnT.SUCCESS;
    }



    public ReturnT<List<XxlJobInfo>> queryJobs(Integer parentId,String executorHandler,String paramKeyword){
        return new ReturnT<>(xxlJobInfoDao.query(parentId,executorHandler,paramKeyword));
    }

    public ReturnT<String> updateLog(XxlJobLog xxlJobLog){
        logger.info(String.format("更新日志结果:%d,%d[updateLog]",xxlJobLog.getId(),xxlJobLog.getHandleCode()));
        xxlJobLogDao.updateChildSummary(xxlJobLog);
        if(xxlJobLog.getHandleCode()>0){
            XxlJobLog log=xxlJobLogDao.load(xxlJobLog.getId());
            if(log.getParentId()!=null && log.getParentId()>0){
                updateChildSummaryByParentId(log.getParentId(),new ArrayList<Integer>());
            }
        }
        return ReturnT.SUCCESS;
    }

    public ReturnT<String> updateJob(XxlJobInfo jobInfo){
        return xxlJobService.update(jobInfo);
    }


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        logger.info(String.format("callback receive:%s",callbackParamList));
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==IJobHandler.SUCCESS.getCode()?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        logger.info(String.format("%d callback前1,%s",handleCallbackParam.getLogId(),handleCallbackParam));
        // valid log item
        XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        logger.info(String.format("%d callback前%s",log.getId(),handleCallbackParam));
        // trigger success, to trigger child job
        String callbackMsg = null;
        int handleCode = handleCallbackParam.getExecuteResult().getCode();
        if (IJobHandler.SUCCESS.getCode() == handleCode) {
            XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
            if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobId())) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                logger.info(String.format("%d触发的子任务:%s",handleCallbackParam.getLogId(),xxlJobInfo.getChildJobId()));
                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {
                        handleCode=0;
                        JobUtils.putParentId(childJobId,log.getId());

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, 0, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }

        logger.info(String.format("%d callback后1%s",log.getId(),handleCallbackParam));

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
        logger.info(String.format("%d callback后2%s",log.getId(),handleCallbackParam));

        log.setHandleCode(handleCode);
        log.setHandleMsg(handleMsg.toString());
        if(log.getHandleCode()==0 && StringUtils.isNotEmpty(handleCallbackParam.getExecuteResult().getContent())){//如果返回结果为进行中
            log.setChildSummary(handleCallbackParam.getExecuteResult().getContent());
        }
        xxlJobLogDao.updateHandleInfo(log);
        logger.info(String.format("更新日志结果:%d,%d[callback本身]",log.getId(),log.getHandleCode()));
        logger.info(String.format("更新日志结果:%d[callback]",log.getParentId()));
        updateChildSummary(log);

        return ReturnT.SUCCESS;
    }

    public void updateChildSummary(XxlJobLog log) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if(xxlJobInfo!=null && xxlJobInfo.getParentId()!=null && xxlJobInfo.getParentId()!=0){
            Integer parentId=log.getParentId();
            if(parentId!=null && parentId>0 && JobUtils.removeChildId(parentId,xxlJobInfo.getId())){
                updateChildSummaryByParentId(parentId);
            }
        }
    }

    /**
     * 根据任务的父id来更新其日志信息
     * @param parentId
     */
    public void updateChildSummaryByParentId(Integer parentId) {
        List<Integer> list= JobUtils.parentIdChildMap.get(parentId);
        if(list==null){
            return;
        }
        updateChildSummaryByParentId(parentId, list);
    }

    private void updateChildSummaryByParentId(Integer parentId, List<Integer> list) {
        synchronized (list){
            int left=list.size();

            List<XxlJobLog> logs=xxlJobLogDao.pageList(0,100,xxlJobLogDao.load(parentId).getJobGroup(),0,null,null,-2, Arrays.asList(parentId));

            int callSuccess=0;
            int callFails=0;
            int ignores=0;
            int triggerFails=0;
            int callSkips=0;
            for(XxlJobLog l:logs){
                if(l.getTriggerCode()==200){
                    if(l.getHandleCode()==200){
                        callSuccess++;
                    }else if(l.getHandleCode()== HandleCodeEnum.IGNORE.getCode()){
                        callSkips++;
                    }else{
                        callFails++;
                    }
                }else if(l.getTriggerCode()== ReturnT.IGNORE_CODE){
                    ignores++;
                }else{
                    triggerFails++;
                }
                if(list.contains(l.getJobId())){
                    left--;
                }
            }

            XxlJobLog toUpdate=new XxlJobLog();
            toUpdate.setId(parentId);
            if(callSuccess==0 && callFails==0 && triggerFails==0 && left==0){//如果子任务的都是正常的跳过
                toUpdate.setHandleCode(HandleCodeEnum.IGNORE.getCode());
            }else if(left==0){
                if(callSuccess>0 && callFails>0){
                    toUpdate.setHandleCode(HandleCodeEnum.CONTAINS_SUCCESS.getCode());
                }else if(callSuccess>0){
                    toUpdate.setHandleCode(200);
                }else {
                    toUpdate.setHandleCode(500);
                }
            }
            String childSummary=String.format("调度[跳过:%d,失败:%d],执行[失败:%d,成功:%d,跳过:%d]",ignores,triggerFails,callFails,callSuccess,callSkips);
            if(left>0){
                childSummary=String.format("运行中:%d,",left)+childSummary;
            }
            toUpdate.setChildSummary(childSummary);
            xxlJobLogDao.updateChildSummary(toUpdate);

            logger.info(String.format("更新日志结果:%d,%d[updateChildSummaryByParentId],%s",toUpdate.getId(),toUpdate.getHandleCode(),list));

            if(left==0){
                JobUtils.parentIdChildMap.remove(parentId);
            }
        }
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            xxlJobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        xxlJobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }

}
