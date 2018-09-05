package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * 提供API管理端接口
 *
 * @author caiyihua
 */
public interface ApiAdminBiz extends AdminBiz {
    /**
     * 新增调度任务
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> add(XxlJobInfo jobInfo);

    /**
     * 修改调度任务
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> update(XxlJobInfo jobInfo);

    /**
     * 删除调度任务
     *
     * @param id
     * @return
     */
    ReturnT<String> remove(int id);

    /**
     * 暂停调度任务
     *
     * @param id
     * @return
     */
    ReturnT<String> pause(int id);

    /**
     * 恢复调度任务
     *
     * @param id
     * @return
     */
    ReturnT<String> resume(int id);
}
