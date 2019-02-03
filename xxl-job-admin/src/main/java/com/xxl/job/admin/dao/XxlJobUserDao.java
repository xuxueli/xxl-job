package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XxlJobUserDao {

    public XxlJobUser loadByName(@Param("username") String username);

    public List<XxlJobUser> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("username") String username,
                                      @Param("permission") int permission);
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("username") String username,
                             @Param("permission") int permission);

    public int add(XxlJobUser xxlJobUser);

    public int update(XxlJobUser xxlJobUser);

    public int delete(@Param("username") String username);
}
