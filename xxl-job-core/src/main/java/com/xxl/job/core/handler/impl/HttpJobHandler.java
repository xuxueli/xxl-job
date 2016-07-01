package com.xxl.job.core.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;

/**
 * HttpJobHandler
 * 
 * @author chenke
 * @date 2016年6月30日 上午11:47:19
 */
public class HttpJobHandler extends IJobHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpJobHandler.class);

    @SuppressWarnings("unused")
    private String        job_group;
    @SuppressWarnings("unused")
    private String        job_name;
    private String        job_path;

    public HttpJobHandler(String job_group, String job_name, String job_path) {
        this.job_group = job_group;
        this.job_name = job_name;
        this.job_path = job_path;
    }

    @Override
    public JobHandleStatus execute(String... params) throws Exception {
        RemoteCallBack callback = null;
        int timeout = 5000;
        if (params != null && params.length >= 1) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String param : params) {
                i++;
                String[] kvs = param.split("=");
                if (kvs.length != 2) {
                    continue;
                }
                if ("timeout".equalsIgnoreCase(kvs[0])) {
                    timeout = Integer.valueOf(kvs[1]);
                    continue;
                }
                if (i == params.length) {
                    sb.append(param);
                } else {
                    sb.append(param).append("&");
                }
            }
            callback = HttpUtil.get(job_path + "?" + sb.toString(), timeout);
        } else {
            callback = HttpUtil.get(job_path, timeout);
        }
        logger.info(job_path + ",status:" + callback.getStatus() + ",msg:" + callback.getMsg());
        if (RemoteCallBack.SUCCESS.equalsIgnoreCase(callback.getStatus())) {
            return JobHandleStatus.SUCCESS;
        }
        throw new RuntimeException(
                job_path + ",status:" + callback.getStatus() + ",msg:" + callback.getMsg());
    }
}
