package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
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
import java.util.concurrent.ConcurrentHashMap;

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

    public final static ConcurrentHashMap<Integer,List<Integer>> childJobParentIdMap=new ConcurrentHashMap<>();


    public final static ConcurrentHashMap<Integer,List<Integer>> parentIdChildMap=new ConcurrentHashMap<>();


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
     * @param jobInfos
     * @return
     */
    public ReturnT<String> addJobs(List<XxlJobInfo> jobInfos){
        List<Integer> parentIds=new ArrayList<>();
        for(XxlJobInfo jobInfo:jobInfos){
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
            List<XxlJobInfo> children=xxlJobInfoDao.query(id,null,null);
            StringBuffer sb=new StringBuffer();
            for(XxlJobInfo job:children){
                sb.append(",").append(job.getId());
            }

            XxlJobInfo jobInfo=xxlJobInfoDao.loadById(id);
            jobInfo.setChildJobId(sb.length()>0?sb.substring(1):"");
            xxlJobInfoDao.update(jobInfo);//自动更新子任务id
        }
        return ReturnT.SUCCESS;
    }


    public ReturnT<List<XxlJobInfo>> queryJobs(Integer parentId,String executorHandler,String paramKeyword){
        return new ReturnT<>(xxlJobInfoDao.query(parentId,executorHandler,paramKeyword));
    }

    public ReturnT<String> updateJob(XxlJobInfo jobInfo){
        return xxlJobService.update(jobInfo);
    }


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==IJobHandler.SUCCESS.getCode()?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    public static Integer getParentId(Integer childId){
        List<Integer> parentIds=childJobParentIdMap.get(childId);
        if(parentIds==null || parentIds.size()==0){
            return null;
        }
        synchronized (parentIds){
            if(parentIds.size()==0){
                return null;
            }
            return parentIds.remove(0);
        }
    }

    public static boolean removeChildId(Integer parentId,Integer childId){
        List<Integer> childIds=parentIdChildMap.get(parentId);
        if(childIds==null || childIds.size()==0){
            return true;
        }
        synchronized (childIds){
            childIds.remove(childIds.indexOf(childId));
            return childIds.size()==0;
        }
    }

    public static void putParentId(Integer childId,Integer parentId){
        putToMap(childId, parentId,childJobParentIdMap);
        putToMap(parentId,childId,parentIdChildMap);
    }

    private static void putToMap(Integer childId, Integer parentId,ConcurrentHashMap<Integer,List<Integer>> map) {
        List<Integer> parentIds=map.get(childId);
        if(parentIds==null){
            parentIds = new ArrayList<>();
            List<Integer> list=map.putIfAbsent(childId,parentIds);
            if(list!=null){
                parentIds=list;
            }
        }
        parentIds.add(parentId);
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

                        putParentId(childJobId,log.getId());
                        ReturnT<String> triggerChildResult = xxlJobService.triggerJob(childJobId);

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
        } else if (IJobHandler.FAIL_RETRY.getCode() == handleCallbackParam.getExecuteResult().getCode()){
            ReturnT<String> retryTriggerResult = xxlJobService.triggerJob(log.getJobId());
            callbackMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_exe_fail_retry") +"<<<<<<<<<<< </span><br>";

            callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_msg1"),
                    (retryTriggerResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")), retryTriggerResult.getMsg());
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

        updateChildSummary(log);

        return ReturnT.SUCCESS;
    }

    public void updateChildSummary(XxlJobLog log) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if(xxlJobInfo!=null && xxlJobInfo.getParentId()!=null && xxlJobInfo.getParentId()!=0){
            if(removeChildId(log.getParentId(),xxlJobInfo.getId())){
                List<XxlJobLog> logs=xxlJobLogDao.pageList(0,100,log.getJobGroup(),0,null,null,-2,log.getParentId());

                int callSuccess=0;
                int callFails=0;
                int ignores=0;
                int triggerFails=0;
                for(XxlJobLog l:logs){
                    if(l.getTriggerCode()==200){
                        if(l.getHandleCode()!=200){
                            callFails++;
                        }else{
                            callSuccess++;
                        }
                    }else if(l.getTriggerCode()==600){
                        ignores++;
                    }else{
                        triggerFails++;
                    }
                }

                XxlJobLog toUpdate=new XxlJobLog();
                toUpdate.setId(log.getParentId());
                toUpdate.setChildSummary(String.format("跳过:%d,调度失败:%d,执行失败:%d,成功:%d",ignores,triggerFails,callFails,callSuccess));
                xxlJobLogDao.updateChildSummary(toUpdate);
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

    @Override
    public ReturnT<String> triggerJob(int jobId) {
        return xxlJobService.triggerJob(jobId);
    }

}
