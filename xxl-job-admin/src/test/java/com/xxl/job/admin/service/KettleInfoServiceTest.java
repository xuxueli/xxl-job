package com.xxl.job.admin.service;

import com.xxl.job.admin.XxlJobAdminApplicationTest;
import com.xxl.job.admin.common.pojo.dto.KettleInfoDTO;
import com.xxl.job.admin.common.utils.FileUtils;
import com.xxl.job.core.enums.KettleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

class KettleInfoServiceTest extends XxlJobAdminApplicationTest {

    @Autowired
    private KettleInfoService kettleInfoService;

    @Test
    void saveKettleInfo() throws IOException {

        KettleInfoDTO kettleInfoDTO = new KettleInfoDTO();
        kettleInfoDTO.setName("测试");
        kettleInfoDTO.setType(KettleType.KTR.name());
        kettleInfoDTO.setFile(FileUtils.file2MultipartFile(new File("G:\\workspace\\kettle脚本\\demo.ktr")));
        kettleInfoDTO.setLogLevel("BASIC");

        kettleInfoService.saveKettleInfo(kettleInfoDTO);

    }
}