package com.xxl.job.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.pojo.dto.HandleLogDTO;
import com.xxl.job.admin.common.pojo.entity.JobLog;
import com.xxl.job.admin.service.AdminApiService;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.admin.service.RegistryService;
import com.xxl.job.admin.thread.CompleteThread;
import com.xxl.job.admin.thread.RegistryThread;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.core.pojo.dto.RegistryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理api服务实现类
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Service
public class AdminApiServiceImpl implements AdminApiService {

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private CompleteThread jobCompleteThread;

    @Autowired
    private RegistryService registryService;

    @Autowired
    private RegistryThread jobRegistryThread;

    @Override
    public void callback(List<HandleCallbackParam> callbackParamList) {
        if (CollectionUtil.isEmpty(callbackParamList)) return;

        jobCompleteThread.pushTask(() -> {
            for (HandleCallbackParam handleCallbackParam : callbackParamList) {
                JobLog jobLog = jobLogService.getById(handleCallbackParam.getLogId());
                if (ObjectUtil.isEmpty(jobLog)) {
                    log.warn("log item not found. param {}", handleCallbackParam);
                    continue;
                }

                if (jobLog.getHandleCode() > NumberConstant.A_NEGATIVE) {
                    log.warn("log repeate callback.. param {}", handleCallbackParam);
                    continue;
                }

                StringBuilder handleMessage = new StringBuilder();
                if (StrUtil.isNotBlank(jobLog.getHandleMessage())) {
                    handleMessage.append(jobLog.getHandleMessage()).append("<br>");
                }
                if (StrUtil.isNotBlank(handleCallbackParam.getHandleMessage())) {
                    handleMessage.append(handleCallbackParam.getHandleMessage());
                }

                // success, save log
                HandleLogDTO handleLogDTO = new HandleLogDTO();
                handleLogDTO.setId(jobLog.getId());
                handleLogDTO.setHandleTime(DateUtil.current());
                handleLogDTO.setHandleCode(handleCallbackParam.getHandleCode());
                handleLogDTO.setHandleMessage(handleMessage.toString());
                jobLogService.updateHandleInfo(handleLogDTO);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void registry(RegistryParam registryParam) {
        String registryKey = registryParam.getRegistryKey();
        String registryValue = registryParam.getRegistryValue();
        String registryGroup = registryParam.getRegistryGroup();
        if (StrUtil.isBlank(registryGroup) || StrUtil.isBlank(registryValue)
                || StrUtil.isBlank(registryKey)) return;

        jobRegistryThread.addRegistry(() -> registryService.syncRegistry(registryGroup, registryKey, registryValue));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void unRegistry(RegistryParam registryParam) {
        String registryKey = registryParam.getRegistryKey();
        String registryValue = registryParam.getRegistryValue();
        String registryGroup = registryParam.getRegistryGroup();
        if (StrUtil.isBlank(registryGroup) || StrUtil.isBlank(registryValue)
                || StrUtil.isBlank(registryKey)) return;
        jobRegistryThread.unRegistry(() -> registryService.deleteRegistry(registryGroup, registryKey, registryValue));
    }
}
