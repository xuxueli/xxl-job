package com.xxl.job.core.glue.loader.impl;

import com.xxl.job.core.glue.loader.GlueLoader;
import com.xxl.job.core.util.DBUtil;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by xuxueli on 16/9/30.
 */
public class DbGlueLoader implements GlueLoader {

    private DataSource dataSource;
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String load(String job_group, String job_name) {
        String sql = "SELECT glue_source FROM XXL_JOB_QRTZ_TRIGGER_INFO WHERE job_group = ? AND job_name = ?";
        List<Map<String, Object>> result = DBUtil.query(dataSource, sql, new String[]{job_group, job_name});
        if (result!=null && result.size()==1 && result.get(0)!=null && result.get(0).get("glue_source")!=null ) {
            return (String) result.get(0).get("glue_source");
        }
        return null;
    }

}
