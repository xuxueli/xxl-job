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
    public List<XxlJobRegistry> findRegistrys(String registryGroup, String registryKey) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("registryGroup", registryGroup);
        params.put("registryKey", registryKey);
        return sqlSessionTemplate.selectList("XxlJobRegistryMapper.findRegistrys", params);
    }

}
