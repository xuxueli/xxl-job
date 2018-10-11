package com.xxl.job.core.biz;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {

    String MAPPING = "/api";

    /**
     * 新增任务
     * @param jobInfo
     * @return
     */
    ReturnT<String> addJob(XxlJobInfo jobInfo);


    ReturnT<String> updateJob(XxlJobInfo jobInfo);

    /**
     * 批量更新
     * @param jobInfos
     * @return
     */
    ReturnT<String> updateJobs(List<XxlJobInfo> jobInfos);

    ReturnT<List<XxlJobInfo>> queryJobs(Integer parentId,String executorHandler,String paramKeyword);

    ReturnT<String> updateLog(XxlJobLog xxlJobLog);

    /**
     * 批量新增，主要用于新增某个父任务下的子任务
     * @param toAdds
     * @return
     */
    public ReturnT<String> addChildJobs(List<XxlJobInfo> toAdds);

    /**
     * 批量删除，主要用于删除某个父任务下的子任务
     * @param toDeletes
     * @return
     */
    public ReturnT<String> deleteChildJobs(List<XxlJobInfo> toDeletes);

    /**
     * 更新某个子任务对应的父任务的childSummary字段，这里的任务指正在运行的任务日志
     * @param log
     */
    void updateChildSummary(XxlJobLog log);



    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registryRemove(RegistryParam registryParam);


    /**
     * 根据任务的父id来更新其日志信息
     * @param parentId
     */
     void updateChildSummaryByParentId(Integer parentId);

}
