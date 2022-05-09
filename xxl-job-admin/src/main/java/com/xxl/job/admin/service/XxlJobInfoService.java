package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobInfoMapper;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class XxlJobInfoService extends ServiceImpl<XxlJobInfoMapper, XxlJobInfo> {

    public IPage<XxlJobInfo> page(int offset,
                                  int pageSize,
                                  int jobGroup,
                                  int triggerStatus,
                                  String jobDesc,
                                  String executorHandler,
                                  String author) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<XxlJobInfo> lambda = queryWrapper.lambda();
        if (jobGroup > 0) {
            lambda.eq(XxlJobInfo::getJobGroup, jobGroup);
        }
        if (triggerStatus >= 0) {
            lambda.eq(XxlJobInfo::getTriggerStatus, triggerStatus);
        }
        if (!StringUtil.isNullOrEmpty(jobDesc)) {
            lambda.like(XxlJobInfo::getJobDesc, jobDesc);
        }
        if (!StringUtil.isNullOrEmpty(executorHandler)) {
            lambda.like(XxlJobInfo::getExecutorHandler, executorHandler);
        }
        if (!StringUtil.isNullOrEmpty(author)) {
            lambda.like(XxlJobInfo::getAuthor, author);
        }
        queryWrapper.orderByDesc("id");
        IPage<XxlJobInfo> iPage = new Page<>(offset / pageSize + 1, pageSize);
        return this.page(iPage, queryWrapper);
    }

    public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getJobGroup, jobGroup);
        return this.list(queryWrapper);
    }

    public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize) {
        QueryWrapper<XxlJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobInfo::getTriggerStatus, 1).le(XxlJobInfo::getTriggerNextTime, maxNextTime);
        queryWrapper.orderByAsc("id");
        IPage<XxlJobInfo> iPage = new Page<>(0, pagesize);
//        queryWrapper.last("  limit " + pagesize);
        iPage = page(iPage, queryWrapper);
        return iPage.getRecords();
    }
}
