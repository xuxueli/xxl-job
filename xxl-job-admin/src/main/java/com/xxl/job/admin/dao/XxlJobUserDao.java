package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XxlJobUserDao {

    public XxlJobUser loadByName(String name);


}
