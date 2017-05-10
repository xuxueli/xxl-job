package com.xxl.job.dao.impl;

import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.util.AdminApiUtil;

/**
 * Created by xuxueli on 17/5/10.
 */
public class AdminApiTest {

    public static void main(String[] args) {
        try {
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), "aaa", "112312312312");
            ReturnT<String> registryResult = AdminApiUtil.callApi("http://localhost:8080/xxl-job-admin"+AdminApiUtil.REGISTRY, registryParam);
            System.out.println(registryResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
