package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.bo.XxlJobInfoBo;
import com.xxl.job.core.biz.model.ReturnT;


/**
 * xxl-job api service
 *
 * @author: Dao-yang.
 * @date: Created in 2025/6/30 11:07
 */
public interface XxlJobApiService {

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> add(XxlJobInfoBo jobInfo);

    /**
     * remove job
     * *
     *
     * @param id
     * @return
     */
    ReturnT<String> remove(int id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    ReturnT<String> start(int id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    ReturnT<String> stop(int id);


}
