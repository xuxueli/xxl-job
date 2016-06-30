package com.xxl.job.core.handler.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.handler.IJobHandler;

/**
 * ShellJobHandler
 * 
 * @author chenke
 * @date 2016年6月30日 上午11:47:19
 */
public class ShellJobHandler extends IJobHandler {
    private static Logger logger = LoggerFactory.getLogger(ShellJobHandler.class);

    private String        job_group;
    private String        job_name;
    private String        job_path;

    public ShellJobHandler(String job_group, String job_name, String job_path) {
        this.job_group = job_group;
        this.job_name = job_name;
        this.job_path = job_path;
    }

    @Override
    public JobHandleStatus execute(String... params) throws Exception {
        Process p = null;
        if (params != null && params.length >= 1) {
            p = Runtime.getRuntime().exec("sh " + job_path + " " + params[0]);
        } else {
            p = Runtime.getRuntime().exec("sh " + job_path);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader ein = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line = null;
        String result = null;
        logger.info(job_group + "_" + job_name + "start run,job path is:" + job_path);
        while ((line = in.readLine()) != null) {
            logger.info(job_path + ":" + line);
            result = line;
        }
        line = null;
        while ((line = ein.readLine()) != null) {
            logger.info(job_path + ":" + line);
        }
        if ("success".equalsIgnoreCase(result)) {
            return JobHandleStatus.SUCCESS;
        }
        return JobHandleStatus.FAIL;
    }

}
