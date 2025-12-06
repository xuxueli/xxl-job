package com.xxl.job.core.context;

/**
 * xxl-job context
 *
 * @author xuxueli 2020-05-21
 * [Dear hj]
 */
public class XxlJobContext {

    public static final int HANDLE_CODE_SUCCESS = 200;
    public static final int HANDLE_CODE_FAIL = 500;
    public static final int HANDLE_CODE_TIMEOUT = 502;

    // ---------------------- base info ----------------------

    /**
     * job id
     */
    private final long jobId;

    /**
     * job param
     */
    private final String jobParam;

    // ---------------------- for log ----------------------

    /**
     * log id
     */
    private final long logId;

    /**
     * log timestamp
     */
    private final long logDateTime;

    /**
     * log filename
     */
    private final String logFileName;

    // ---------------------- for shard ----------------------

    /**
     * shard index
     */
    private final int shardIndex;

    /**
     * shard total
     */
    private final int shardTotal;

    // ---------------------- for handle ----------------------

    /**
     * handleCode：The result status of job execution
     *
     *      200 : success
     *      500 : fail
     *      502 : timeout
     *
     */
    private int handleCode;

    /**
     * handleMsg：The simple log msg of job execution
     */
    private String handleMsg;


    public XxlJobContext(long jobId,
                         String jobParam,
                         long logId,
                         long logDateTime,
                         String logFileName,
                         int shardIndex, 
                         int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.logId = logId;
        this.logDateTime = logDateTime;
        this.logFileName = logFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HANDLE_CODE_SUCCESS;  // default success
    }

    public long getJobId() {
        return jobId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public long getLogId() {
        return logId;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public int getShardTotal() {
        return shardTotal;
    }

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public int getHandleCode() {
        return handleCode;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    // ---------------------- tool ----------------------

    /**
     * xxl-job context store
     */
    private static final InheritableThreadLocal<XxlJobContext> contextHolder = new InheritableThreadLocal<XxlJobContext>(); // support for child thread of job handler)

    /**
     * set xxl-job context
     */
    public static void setXxlJobContext(XxlJobContext xxlJobContext){
        contextHolder.set(xxlJobContext);
    }

    /**
     * get xxl-job context
     */
    public static XxlJobContext getXxlJobContext(){
        return contextHolder.get();
    }

}