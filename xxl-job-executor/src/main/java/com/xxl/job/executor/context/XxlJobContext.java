package com.xxl.job.executor.context;

import com.xxl.job.core.enums.ResponseEnum;
import lombok.Data;

/**
 * xxl-job Context
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
public class XxlJobContext {

    /**
     * job id
     */
    private final long jobId;

    /**
     * job param
     */
    private final String jobParam;

    /**
     * job log filename
     */
    private final String jobLogFileName;

    /**
     * shard index
     */
    private final int shardIndex;

    /**
     * shard total
     */
    private final int shardTotal;

    /**
     * handleCode：The result status of job execution
     * <p>
     * 0 : success
     * 500 : fail
     * 502 : timeout
     */
    private int handleCode;

    /**
     * handleMsg：The simple log msg of job execution
     */
    private String handleMessage;

    public XxlJobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;
        this.handleCode = ResponseEnum.SUCCESS.getCode();
    }

    /**
     * support for child thread of job handler
     */
    private static InheritableThreadLocal<XxlJobContext> contextHolder = new InheritableThreadLocal<>();

    public static void setJobContext(XxlJobContext xxlJobContext) {
        contextHolder.set(xxlJobContext);
    }

    public static XxlJobContext getJobContext() {
        return contextHolder.get();
    }





}