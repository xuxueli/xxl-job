package com.xxl.job.admin.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XxlJobLockDao {
    public void lock();
}
