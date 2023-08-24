package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 注册配置
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Getter
@AllArgsConstructor
public enum RegistryConfig {

    BEAT_TIMEOUT(30),
    DEAD_TIMEOUT(BEAT_TIMEOUT.value * 3),


    ;


    private final Integer value;

}
