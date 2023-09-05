package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.xxl.job.admin.common.pojo.dto.GlueLogDTO;
import com.xxl.job.admin.common.pojo.entity.GlueLog;
import com.xxl.job.admin.common.pojo.vo.GlueLogVO;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.mapper.GlueLogMapper;
import com.xxl.job.admin.service.GlueLogService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * GLUE日志 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Service
public class GlueLogServiceImpl extends BaseServiceImpl<GlueLogMapper, GlueLog, GlueLog, GlueLogVO> implements GlueLogService {

    @Autowired
    private GlueLogMapper glueLogMapper;

    @Autowired
    private JobInfoService jobInfoService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveGlue(GlueLogDTO glueLogDTO) {
        JobInfoVO jobInfoVO = jobInfoService.queryById(glueLogDTO.getJobId());
        Assert.notNull(jobInfoVO, ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        jobInfoService.updateGlueById(jobInfoVO.getId(), glueLogDTO.getGlueType(), glueLogDTO.getGlueSource(), glueLogDTO.getDescription());

        GlueLog glueLog = new GlueLog();
        BeanUtil.copyProperties(glueLogDTO, glueLog);
        glueLog.setCreatedTime(DateUtil.current());
        glueLog.setDescription(jobInfoVO.getGlueDescription());

        this.save(glueLog);

        // remove code backup more than 30
        glueLogMapper.deleteOldGlueLog(jobInfoVO.getId(), 30);
    }

    @Override
    public List<GlueLogVO> findGlueLogByJobId(Long jobId) {
        Assert.notNull(jobId, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        return this.objectConversion(glueLogMapper.findGlueLogByJobId(jobId));
    }

    @Override
    public void deleteOldGlueLog(Long jobId, int limit) {
        Assert.notNull(jobId, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        glueLogMapper.deleteOldGlueLog(jobId, limit);
    }

    @Override
    public void deleteGlueLogByJobId(Long jobId) {
        Assert.notNull(jobId, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        glueLogMapper.deleteGlueLogByJobId(jobId);
    }
}
