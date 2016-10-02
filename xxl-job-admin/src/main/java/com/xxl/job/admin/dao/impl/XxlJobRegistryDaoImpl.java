package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

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

}
