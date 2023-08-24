package com.xxl.job.executor.service.impl;

import com.xxl.job.executor.service.GlueGroovyClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认GLUE groovy类
 *
 * @author Rong.Jia
 * @date 2023/05/22
 */
@Slf4j
@Component
public class DefaultGlueGroovyClass implements GlueGroovyClass {

    @Override
    public void execute(Long jobId) {

    }
}
