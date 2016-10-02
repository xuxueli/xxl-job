package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public class XxlJobRegistryDaoImpl implements IXxlJobRegistryDao {

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int removeDead(int timeout) {
        return sqlSessionTemplate.delete("XxlJobRegistryMapper.removeDead", timeout);
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout) {
        return sqlSessionTemplate.selectList("XxlJobRegistryMapper.findAll", timeout);
    }

    @Override
    public int registryUpdate(String registryGroup, String registryKey, String registryValue) {
        Map<String, Object> params = new HashMap();
        params.put("registryGroup", registryGroup);
        params.put("registryKey", registryKey);
        params.put("registryValue", registryValue);

        return sqlSessionTemplate.update("XxlJobRegistryMapper.registryUpdate", params);
    }

    @Override
    public int registrySave(String registryGroup, String registryKey, String registryValue) {
        Map<String, Object> params = new HashMap();
        params.put("registryGroup", registryGroup);
        params.put("registryKey", registryKey);
        params.put("registryValue", registryValue);

        return sqlSessionTemplate.update("XxlJobRegistryMapper.registrySave", params);
    }

}
