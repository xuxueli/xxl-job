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

    public static final String MAPPING = "/api";

    /**
     * 新增任务
     * @param jobInfo
     * @return
     */
    public ReturnT<String> addJob(XxlJobInfo jobInfo);

    ReturnT<List<XxlJobInfo>> queryJobs(Integer parentId,String executorHandler,String paramKeyword);

    /**
     * 批量新增，主要用于新增某个父任务下的子任务
     * @param jobInfos
     * @return
     */
    public ReturnT<String> addJobs(List<XxlJobInfo> jobInfos);

    void updateChildSummary(XxlJobLog log);

    ReturnT<String> updateJob(XxlJobInfo jobInfo);

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);

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
     * trigger job for once
     *
     * @param jobId
     * @return
     */
    public ReturnT<String> triggerJob(int jobId);

}
