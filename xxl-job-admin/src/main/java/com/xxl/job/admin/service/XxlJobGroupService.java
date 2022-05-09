package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class XxlJobGroupService extends ServiceImpl<XxlJobGroupMapper, XxlJobGroup> {

    @Override
    public List<XxlJobGroup> list() {
        QueryWrapper<XxlJobGroup> xxlJobGroupQueryWrapper = new QueryWrapper<>();
        xxlJobGroupQueryWrapper.orderByDesc("app_name", "title", "id");
        return this.list(xxlJobGroupQueryWrapper);
    }

    public IPage<XxlJobGroup> page(int start, int pageSize, String appName, String title) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<XxlJobGroup> lambda = queryWrapper.lambda();
        if (null != appName && !appName.isEmpty()) {
            lambda.like(XxlJobGroup::getAppName, appName);
        }
        if (null != title && !title.isEmpty()) {
            lambda.like(XxlJobGroup::getTitle, title);
        }
        queryWrapper.orderByDesc("app_name", "title", "id");
        IPage<XxlJobGroup> iPage = new Page<>(start, pageSize);
        return this.page(iPage, queryWrapper);
    }

    public List<XxlJobGroup> findByAddressType(int addressType) {
        QueryWrapper<XxlJobGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobGroup::getAddressType, addressType);
        queryWrapper.orderByDesc("app_name", "title", "id");
        return this.list(queryWrapper);
    }
}
