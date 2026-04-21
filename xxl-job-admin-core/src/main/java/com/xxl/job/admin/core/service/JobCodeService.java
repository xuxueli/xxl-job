package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;

import java.util.List;

public interface JobCodeService {

    /**
     * Load job info and glue code
     * @return job info with glue source loaded, null if not found
     */
    XxlJobInfo loadWithCode(int jobId);

    /**
     * Update GLUE code
     * @return true if success
     */
    boolean updateCode(int jobId, String glueSource, String glueRemark, int userId);

    /**
     * Find glue history records
     */
    List<XxlJobLogGlue> findJobLogGlues(int jobId);
}