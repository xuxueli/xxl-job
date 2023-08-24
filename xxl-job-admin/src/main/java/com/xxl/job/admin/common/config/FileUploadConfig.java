package com.xxl.job.admin.common.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.File;


/**
 * 文件上传配置
 *
 * @author Rong.Jia
 * @date 2023/08/24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xdc.multipart")
public class FileUploadConfig {

    private static final String TMP_DIR = System.getProperty("user.dir") + "/data/tmp";

    /**
     *  是否开启， 默认：false
     */
    private boolean enabled = false;

    /**
     * 路径， 默认：System.getProperty("user.dir") + "/data/tmp";
     */
    private String location;

    /**
     *  路径是否在当前项目的根目录生成
     */
    private Boolean isProjectRoot = Boolean.TRUE;

    /**
     * 文件大小限制，单位：M 默认： 10M
     */
    private Long maxFileSize = 10L;

    /**
     * 设置总上传数据总大小，单位：M 默认： 10M
     */
    private Long maxRequestSize = 10L;

    /**
     * 设置磁盘写入的限制，单位：M 默认： 20M
     */
    private Long fileSizeThreshold = 20L;

    @Bean
    public MultipartConfigElement multipartConfigElement() {

        MultipartConfigFactory factory = new MultipartConfigFactory();

        //路径有可能限制
        File tmpFile = new File(getLocation());
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }

        factory.setLocation(location);
        factory.setFileSizeThreshold(DataSize.ofMegabytes(fileSizeThreshold));
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));
        return factory.createMultipartConfig();
    }

    public String getLocation() {
        String path = TMP_DIR;
        if (StrUtil.isNotBlank(location)) {
            path = isProjectRoot ? System.getProperty("user.dir") + location : location;
        }
        return path;
    }
}
