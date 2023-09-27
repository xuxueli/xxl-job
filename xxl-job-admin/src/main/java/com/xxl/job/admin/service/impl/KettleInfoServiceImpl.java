package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.VersionComparator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.*;
import com.xxl.job.admin.common.constants.FileConstant;
import com.xxl.job.admin.common.constants.NumberConstant;
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
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.KettleInfoService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.core.enums.KettleType;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public KettleInfoVO queryById(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        return this.objectConversion(this.getById(id));
    }

    @Override
    public Boolean delete(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        Assert.isFalse(jobInfoService.existJobInfoByKettleId(Convert.toLong(id)),
                ResponseEnum.THE_TASK_IS_BOUND_TO_THE_MODEL.getMessage());
        return super.delete(id);
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
        kettleInfo.setGuideKjb(FileUtil.getName(kettleInfoDTO.getGuideKjb()));

        MultipartFile file = kettleInfoDTO.getFile();
        if (ObjectUtil.isNotNull(file) && !file.isEmpty()) {
            kettleInfo.setKettleFile(setRelativePath(file));
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
        kettleInfo.setCreatedTime(DateUtil.date());
        this.save(kettleInfo);
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

    @Override
    public List<KettleInfoVO> findKettleAdvancedVersionById(Long id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        KettleInfo kettleInfo = this.getById(id);
        Assert.notNull(kettleInfo, ResponseEnum.THE_KETTLE_DOES_NOT_EXIST.getMessage());

        List<KettleInfo> kettleInfos = kettleInfoMapper.findKettleBySeries(kettleInfo.getSeries());
        if (CollectionUtil.isNotEmpty(kettleInfos)) {
            return kettleInfos.stream()
                    .filter(a -> VersionComparator.INSTANCE.compare(a.getVersion(), kettleInfo.getVersion()) > NumberConstant.ZERO)
                    .map(this::objectConversion)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 设置相对路径
     * @param multipartFile 文件
     * @return {@link byte[]}
     */
    private byte[] setRelativePath(MultipartFile multipartFile) {
        Assert.isFalse(ObjectUtil.isNull(multipartFile) || multipartFile.isEmpty(), ResponseEnum.FILE_DOES_NOT_EXIST.getMessage());
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename.endsWith(FileConstant.ZIP_FILE_SUFFIX)) {
            File unzip = null;
            try {
                unzip = ZipUtil.unzip(multipartFile.getInputStream(),
                        new File(FileConstant.TMP_DIR + StrUtil.SLASH + IdUtil.fastSimpleUUID()), CharsetUtil.defaultCharset());
            }catch (Exception e) {
                log.error("文件读取异常 {}", e.getMessage());
                throw new XxlJobAdminException(ResponseEnum.FILE_DOES_NOT_EXIST);
            }

            List<File> files = FileUtil.loopFiles(unzip, File::isFile);

            if (CollectionUtil.isEmpty(files)) {
                log.error("压缩包内文件为空, 请检查");
                throw new XxlJobAdminException(ResponseEnum.THE_FILES_IN_THE_COMPRESSED_PACKAGE_ARE_EMPTY);
            }

            if (files.stream().noneMatch(a -> StrUtil.equalsIgnoreCase(KettleType.KJB.name(), FileUtil.extName(a)))) {
                log.error("压缩包内没有'kjb'文件, 请检查");
                throw new XxlJobAdminException(ResponseEnum.THERE_IS_NO_KJB_FILE_IN_THE_ZIP_PACKAGE);
            }

            String newDir = FileConstant.TMP_DIR + FileUtil.FILE_SEPARATOR + IdUtil.fastSimpleUUID();
            FileUtil.mkdir(newDir);

            for (File file : files) {
                if (StrUtil.equalsIgnoreCase(KettleType.KJB.name(), FileUtil.extName(file))) {
                    try {
                        Document doc = new SAXReader().read(file);
                        Element rootElement = doc.getRootElement();
                        List<Element> entries = rootElement.elements("entries");

                        for (Element entry : entries) {
                            List<Element> elements = entry.elements("entry");
                            for (Element element : elements) {
                                List<Element> filenames = element.elements("filename");
                                for (Element filename : filenames) {
                                    String text = filename.getText();
                                    String newText = "${Internal.Entry.Current.Directory}" +
                                            StrUtil.sub(text, StrUtil.lastIndexOfIgnoreCase(text, StrUtil.SLASH), text.length());
                                    filename.setText(newText);
                                }
                            }
                        }

                        FileOutputStream out =new FileOutputStream(newDir + StrUtil.SLASH + file.getName());
                        OutputFormat format = OutputFormat.createPrettyPrint();
                        format.setEncoding(CharsetUtil.UTF_8);
                        XMLWriter writer=new XMLWriter(out, format);
                        writer.write(doc);
                        writer.close();
                    }catch (Exception e) {
                        log.error("文件相对路径处理异常 {}", e.getMessage());
                        throw new XxlJobAdminException(ResponseEnum.FILE_RELATIVE_PATH_PROCESSING_EXCEPTION);
                    }
                }else {
                    FileUtil.move(file, new File(newDir + StrUtil.SLASH + file.getName()), Boolean.TRUE);
                }
            }

            File zip = null;
            try {
                zip = ZipUtil.zip(new File(newDir + FileConstant.ZIP_FILE_SUFFIX), Boolean.FALSE, new File(newDir));
            }catch (Exception e) {
                log.error("文件重新打压缩包异常 {}", e.getMessage());
                throw new XxlJobAdminException(ResponseEnum.THE_FILE_RECOMPRESSED_PACKAGE_IS_ABNORMAL_PROCEDURE);
            }

            byte[] bytes = FileUtil.readBytes(zip);
            try {
                FileUtil.del(zip);
                FileUtil.del(unzip);
                FileUtil.del(newDir);
            }catch (Exception ignored){}
            return bytes;
        }else {
            return getBytes(multipartFile);
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
