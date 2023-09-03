package com.xxl.job.executor.factory.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.xxl.job.core.enums.KettleLogLevel;
import com.xxl.job.executor.utils.KettleUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * kettle ktr任务 handler
 *
 * @author Rong.Jia
 * @date 2023/08/30
 */
@Slf4j
@AllArgsConstructor
public class KettleKtrJobHandler extends JobHandler {

    private Long jobId;

    private String jobName;

    /**
     * kettle ktr 文件
     */
    private byte[] ktr;

    /**
     * kettle 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */
    private KettleLogLevel logLevel;

    @Override
    public void init() throws Exception {

    }

    @Override
    public void execute(Object param) throws Exception {
        Map<String, String> params = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(param)) {
            params = JSONObject.parseObject(JSON.toJSONString(param), new TypeReference<Map<String, String>>() {});
        }
        KettleUtils.callTrans(jobName, ktr, params, logLevel);
    }

    @Override
    public void destroy() throws Exception {

    }
}
