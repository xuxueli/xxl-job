package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.KettleInfoDTO;
import com.xxl.job.admin.common.pojo.entity.KettleInfo;
import com.xxl.job.admin.common.pojo.vo.KettleInfoVO;
import com.xxl.job.admin.service.base.BaseService;

/**
 * <p>
 * kettle信息 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
public interface KettleInfoService extends BaseService<KettleInfo, KettleInfo, KettleInfoVO> {

    /**
     * 保存kettle
     * @param kettleInfoDTO kettle信息DTO
     */
    void saveKettleInfo(KettleInfoDTO kettleInfoDTO);

    /**
     * 根据ID修改状态
     * @param id 主键
     * @param status 状态, 1: 启用, 0:禁用
     */
    void updateStatusById(Long id, Integer status);












}
