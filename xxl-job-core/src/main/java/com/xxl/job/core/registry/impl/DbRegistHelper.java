package com.xxl.job.core.registry.impl;

import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.util.DBUtil;

import javax.sql.DataSource;

/**
 * Created by xuxueli on 16/9/30.
 */
public class DbRegistHelper implements RegistHelper {

    private DataSource dataSource;
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int registry(String registGroup, String registryKey, String registryValue) {
        String updateSql = "UPDATE XXL_JOB_QRTZ_TRIGGER_REGISTRY SET `update_time` = NOW() WHERE `registry_group` = ? AND `registry_key` = ? AND `registry_value` = ?";
        String insertSql = "INSERT INTO XXL_JOB_QRTZ_TRIGGER_REGISTRY( `registry_group` , `registry_key` , `registry_value`, `update_time`) VALUES(? , ? , ?, NOW())";
        int ret = DBUtil.update(dataSource, updateSql, new Object[]{registGroup, registryKey, registryValue});
        if (ret<1) {
            ret = DBUtil.update(dataSource, insertSql, new Object[]{registGroup, registryKey, registryValue});
        }
        return ret;
    }
}
