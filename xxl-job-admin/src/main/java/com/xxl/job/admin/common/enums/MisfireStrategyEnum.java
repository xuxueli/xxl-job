package com.xxl.job.admin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 调度过期策略
 *
 * @author Rong.Jia
 * @date 2023/05/13
 */
@Getter
@AllArgsConstructor
public enum MisfireStrategyEnum {

    // 忽略
    DO_NOTHING,

    // 立即执行一次
    FIRE_ONCE_NOW,


    ;

    public static MisfireStrategyEnum match(String name) {
        return Arrays.stream(MisfireStrategyEnum.values())
                .filter(a -> a.name().equals(name))
                .findAny().orElse(DO_NOTHING);
    }

}
