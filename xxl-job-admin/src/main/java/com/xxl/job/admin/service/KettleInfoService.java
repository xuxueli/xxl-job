package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.KettleInfoDTO;
import com.xxl.job.admin.common.pojo.entity.KettleInfo;
import com.xxl.job.admin.common.pojo.vo.KettleInfoVO;
import com.xxl.job.admin.service.base.BaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
     * 下载文件
     *
     * @param id       主键ID
     * @param request 请求
     * @param response 响应
     */
    void download(Long id, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据ID查询更高版本的模型
     *
     * @param id 主键
     * @return {@link List}<{@link KettleInfoVO}>
     */
    List<KettleInfoVO> findKettleAdvancedVersionById(Long id);


















}
