package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public class XxlJobGroupDaoImpl implements IXxlJobGroupDao {

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<XxlJobGroup> findAll() {
        return sqlSessionTemplate.selectList("XxlJobGroupMapper.findAll");
    }

    @Override
    public int save(XxlJobGroup xxlJobGroup) {
        return sqlSessionTemplate.update("XxlJobGroupMapper.save", xxlJobGroup);
    }

    @Override
    public int update(XxlJobGroup xxlJobGroup) {
        return sqlSessionTemplate.update("XxlJobGroupMapper.update", xxlJobGroup);
    }

    @Override
    public int remove(String appName) {
        return sqlSessionTemplate.delete("XxlJobGroupMapper.remove", appName);
    }

    @Override
    public XxlJobGroup load(String appName) {
        return sqlSessionTemplate.selectOne("XxlJobGroupMapper.load", appName);
    }


}
