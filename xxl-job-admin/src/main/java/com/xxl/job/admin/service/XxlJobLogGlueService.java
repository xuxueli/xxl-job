package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.XxlJobLogGlueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class XxlJobLogGlueService extends ServiceImpl<XxlJobLogGlueMapper, XxlJobLogGlue> {

    public List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId) {
        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        queryWrapper.orderByDesc("id");
        return this.list(queryWrapper);
    }

    public boolean removeOld(@Param("jobId") int jobId, @Param("limit") int limit) {
        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        queryWrapper.orderByDesc("update_time");
        IPage<XxlJobLogGlue> iPage = new Page<>(0, limit);
        iPage = page(iPage, queryWrapper);
        List<XxlJobLogGlue> removeList = iPage.getRecords();
        Set<Integer> removeId = removeList.stream().map(XxlJobLogGlue::getId).collect(Collectors.toSet());
        queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId)
                .notIn(XxlJobLogGlue::getId, removeId);
        return this.remove(queryWrapper);
    }

    public boolean deleteByJobId(@Param("jobId") int jobId) {
        QueryWrapper<XxlJobLogGlue> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogGlue::getJobId, jobId);
        return this.remove(queryWrapper);
    }
}
