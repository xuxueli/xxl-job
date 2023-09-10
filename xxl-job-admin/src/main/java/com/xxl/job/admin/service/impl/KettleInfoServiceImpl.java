package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.exceptions.XxlJobAdminException;
import com.xxl.job.admin.common.pojo.dto.KettleInfoDTO;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.entity.KettleInfo;
import com.xxl.job.admin.common.pojo.query.KettleInfoQuery;
import com.xxl.job.admin.common.pojo.vo.KettleInfoVO;
import com.xxl.job.admin.common.utils.AuthUtils;
import com.xxl.job.admin.common.utils.VersionUtils;
import com.xxl.job.admin.mapper.KettleInfoMapper;
import com.xxl.job.admin.service.KettleInfoService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.core.enums.KettleType;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * kettle信息 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@Slf4j
@Service
public class KettleInfoServiceImpl extends BaseServiceImpl<KettleInfoMapper, KettleInfo, KettleInfo, KettleInfoVO> implements KettleInfoService {

    private static final String VERSION = "1.0";

    @Autowired
    private KettleInfoMapper kettleInfoMapper;

    @Override
    public KettleInfoVO queryById(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        return this.objectConversion(this.getById(id));
    }

    @Override
    public List<KettleInfo> queryList(PageDTO pageDTO) {
        KettleInfoQuery kettleInfoQuery = new KettleInfoQuery();
        BeanUtil.copyProperties(pageDTO, kettleInfoQuery);
        return kettleInfoMapper.findKettle(kettleInfoQuery);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveKettleInfo(KettleInfoDTO kettleInfoDTO) {

        String name = kettleInfoDTO.getName();
        String version = VERSION;
        String maxVersion = kettleInfoMapper.findMaxVersionByName(name);
        if (StrUtil.isNotBlank(maxVersion)) {
            version = VersionUtils.autoUpgradeVersion(maxVersion);
        }

        Assert.isNull(kettleInfoMapper.findKettleByNameAndVersion(name, version), ResponseEnum.THE_KETTLE_ALREADY_EXISTS.getMessage());

        KettleInfo kettleInfo = new KettleInfo();
        BeanUtil.copyProperties(kettleInfoDTO, kettleInfo);

        MultipartFile file = kettleInfoDTO.getFile();
        if (ObjectUtil.isNotNull(file) && !file.isEmpty()) {
            kettleInfo.setKettleFile(getBytes(file));
            kettleInfo.setFileName(file.getOriginalFilename());
        }

        if (KettleType.KJB.equals(KettleType.valueOf(kettleInfo.getType()))) {
            Assert.notNull(kettleInfoDTO.getGuideKjb(), ResponseEnum.THE_KJB_BOOT_FILE_CANNOT_BE_EMPTY.getMessage());
        }

        kettleInfo.setVersion(version);
        kettleInfo.setCreatedTime(DateUtil.current());
        this.save(kettleInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateStatusById(Long id, Integer status) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        KettleInfo kettleInfo = this.getById(id);
        Assert.notNull(kettleInfo, ResponseEnum.THE_KETTLE_DOES_NOT_EXIST.getMessage());
        kettleInfo.setUpdatedTime(DateUtil.current());
        kettleInfo.setUpdatedUser(AuthUtils.getCurrentUser());
        kettleInfo.setStatus(status);
        this.updateById(kettleInfo);
    }

    private byte[] getBytes(MultipartFile file) {
        Assert.isFalse(ObjectUtil.isNull(file) || file.isEmpty(), ResponseEnum.FILE_DOES_NOT_EXIST.getMessage());
        try {
            return file.getBytes();
        }catch (Exception e) {
            log.error("getBytes {}", e.getMessage());
            throw new XxlJobAdminException(ResponseEnum.FILE_DOES_NOT_EXIST);
        }
    }

}
