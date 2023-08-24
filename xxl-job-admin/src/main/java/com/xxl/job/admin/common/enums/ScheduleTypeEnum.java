package com.xxl.job.admin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 调度类型
 *
 * @author Rong.Jia
 * @date 2023/05/13
 */
@Getter
@AllArgsConstructor
public enum ScheduleTypeEnum {

    // 无
    NONE,

    // cron
    CRON,

    // 固定速度, 单位；秒
    FIX_RATE,



    ;


    public static ScheduleTypeEnum match(String name) {
        return Arrays.stream(ScheduleTypeEnum.values())
                .filter(a -> a.name().equals(name))
                .findAny().orElse(NONE);
    }

}
