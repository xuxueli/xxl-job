package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class XxlJobUserService extends ServiceImpl<XxlJobUserMapper, XxlJobUser> {

    public IPage<XxlJobUser> page(@Param("offset") int offset,
                                  @Param("pagesize") int pagesize,
                                  @Param("username") String username,
                                  @Param("role") int role) {
        QueryWrapper<XxlJobUser> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<XxlJobUser> lambda = queryWrapper.lambda();
        if (role > -1) {
            lambda.eq(XxlJobUser::getRole, role);
        }
        if (null != username && !username.isEmpty()) {
            lambda.like(XxlJobUser::getUsername, username);
        }
        queryWrapper.orderByAsc(" username");
        IPage<XxlJobUser> page = new Page<>(offset / pagesize + 1, pagesize);
        return page(page, queryWrapper);
    }

    public XxlJobUser loadByUserName(@Param("username") String username) {
        QueryWrapper<XxlJobUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobUser::getUsername, username);
        List<XxlJobUser> list = list(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }
}
