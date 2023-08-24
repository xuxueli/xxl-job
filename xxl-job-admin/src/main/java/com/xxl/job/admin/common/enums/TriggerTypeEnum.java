package com.xxl.job.admin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 触发枚举类型
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Getter
@AllArgsConstructor
public enum TriggerTypeEnum {

    // 手动触发
    MANUAL("手动触发"),
    CRON("Cron触发"),
    RETRY("失败重试触发"),
    PARENT("父任务触发"),
    API("API触发"),
    MISFIRE("调度过期补偿"),



    ;

    private final String value;




}
