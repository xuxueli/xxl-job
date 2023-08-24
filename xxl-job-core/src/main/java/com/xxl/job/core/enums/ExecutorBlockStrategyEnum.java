package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 阻塞处理策略
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Getter
@AllArgsConstructor
public enum ExecutorBlockStrategyEnum {

    // 单机串联
    SERIAL_EXECUTION("Serial execution"),

    // 丢弃后续调度
    DISCARD_LATER("Discard Later"),

    // 覆盖之前调度
    COVER_EARLY("Cover Early");

    private final String title;

    public static ExecutorBlockStrategyEnum match(String name) {
        return Arrays.stream(ExecutorBlockStrategyEnum.values())
                .filter(a -> a.name().equals(name))
                .findAny().orElse(SERIAL_EXECUTION);
    }

}
