package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * kettle日志级别
 *
 * @author Rong.Jia
 * @date 2023/08/30
 */
@Getter
@AllArgsConstructor
public enum KettleLogLevel {

    /*
     * kettle 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */

    NOTHING,ERROR,MINIMAL,BASIC,DETAILED,DEBUG,ROWLEVEL

    ;

    public static KettleLogLevel match(String name) {
        return Arrays.stream(KettleLogLevel.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny().orElse(BASIC);
    }











}
