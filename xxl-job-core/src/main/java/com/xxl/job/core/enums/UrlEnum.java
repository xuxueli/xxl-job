package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * url枚举
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Getter
@AllArgsConstructor
public enum UrlEnum implements IUrlEnum {

    // 任务配置
    JOB_CONFIG_DETAILS("/job-config-details/job/{jobId}"),

    // 回调
    ADMIN_CALLBACK("/admin/callback"),

    // 注册
    ADMIN_REGISTRY("/admin/registry"),

    // 删除注册
    ADMIN_UNREGISTRY("/admin/unRegistry"),

    // 心跳
    EXECUTOR_BEAT("/executor/beat"),

    // 空闲心跳
    EXECUTOR_IDLE_BEAT("/executor/idleBeat"),

    // 运行
    EXECUTOR_RUN("/executor/run"),

    // 停止
    EXECUTOR_KILL("/executor/kill"),

    // 日志
    EXECUTOR_LOG("/executor/log"),









    ;




    private final String value;

    @Override
    public String getValue() {
        return this.value;
    }
}
