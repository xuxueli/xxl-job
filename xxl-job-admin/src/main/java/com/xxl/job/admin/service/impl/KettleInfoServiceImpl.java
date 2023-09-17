package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.admin.common.exceptions.XxlJobAdminException;
import com.xxl.job.admin.common.pojo.bo.KettleMaxVersionBO;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.net.URLEncoder;
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
        String series = IdUtil.fastSimpleUUID();
        KettleMaxVersionBO kettleMaxVersionBO = kettleInfoMapper.findMaxVersionByName(name);
        if (ObjectUtil.isNotNull(kettleMaxVersionBO)) {
            version = VersionUtils.autoUpgradeVersion(kettleMaxVersionBO.getMaxVersion());
            series = kettleMaxVersionBO.getSeries();
        }

        Assert.isNull(kettleInfoMapper.findKettleByNameAndVersion(name, version),
                ResponseEnum.THE_KETTLE_ALREADY_EXISTS.getMessage());

        KettleInfo kettleInfo = new KettleInfo();
        BeanUtil.copyProperties(kettleInfoDTO, kettleInfo);

        MultipartFile file = kettleInfoDTO.getFile();
        if (ObjectUtil.isNotNull(file) && !file.isEmpty()) {
            kettleInfo.setKettleFile(getBytes(file));
            kettleInfo.setFileName(file.getOriginalFilename());
        }else {
            KettleInfo lastVersion = kettleInfoMapper.findKettleByNameAndVersion(kettleMaxVersionBO.getName(), kettleMaxVersionBO.getMaxVersion());
            if (ObjectUtil.isNotNull(lastVersion)) {
                kettleInfo.setKettleFile(lastVersion.getKettleFile());
                kettleInfo.setFileName(lastVersion.getFileName());
                kettleInfo.setGuideKjb(lastVersion.getGuideKjb());
            }
        }

        if (KettleType.KJB.equals(KettleType.valueOf(kettleInfo.getType()))) {
            Assert.notNull(kettleInfoDTO.getGuideKjb(), ResponseEnum.THE_KJB_BOOT_FILE_CANNOT_BE_EMPTY.getMessage());
        }

        kettleInfo.setVersion(version);
        kettleInfo.setSeries(series);
        kettleInfo.setCode(IdUtil.fastSimpleUUID());
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

    @Override
    public void download(Long id, HttpServletRequest request, HttpServletResponse response) {
        KettleInfo kettleInfo = this.getById(id);
        if (ObjectUtil.isNotNull(kettleInfo)) {
            try {
                response.setContentType("multipart/form-data");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html");
                setAttachmentCoding(request, response, kettleInfo.getFileName());
                IoUtil.write(response.getOutputStream(), Boolean.TRUE, kettleInfo.getKettleFile());
            }catch (Exception e) {
                log.error("文件 {} : 下载异常 {}", kettleInfo.getFileName(), e.getMessage());
                throw new XxlJobAdminException(ResponseEnum.ERROR.getCode(), String.format("文件 【%s】 下载异常!, 请重试", kettleInfo.getFileName()));
            }
        }
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

    private void setAttachmentCoding(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception {
        String browser = request.getHeader("User-Agent");
        if (-1 < browser.indexOf("MSIE 6.0") || -1 < browser.indexOf("MSIE 7.0")) {
            // IE6, IE7 浏览器
            response.addHeader("content-disposition", "attachment;filename="
                    + new String(fileName.getBytes(), "ISO8859-1"));
        } else if (-1 < browser.indexOf("MSIE 8.0")) {
            // IE8
            response.addHeader("content-disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
        } else if (-1 < browser.indexOf("MSIE 9.0")) {
            // IE9
            response.addHeader("content-disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
        } else if (-1 < browser.indexOf("Chrome")) {
            // 谷歌
            response.addHeader("content-disposition",
                    "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        } else if (-1 < browser.indexOf("Safari")) {
            // 苹果
            response.addHeader("content-disposition", "attachment;filename="
                    + new String(fileName.getBytes(), "ISO8859-1"));
        } else {
            // 火狐或者其他的浏览器
            response.addHeader("content-disposition",
                    "attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        }
    }


}
