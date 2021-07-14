package com.xxl.job.core.context;

/**
 * xxl-job context
 *
 * @author xuxueli 2020-05-21
 * [Dear hj]
 */
public class XxlJobContext {

    public static final int HANDLE_COCE_SUCCESS = 200;
    public static final int HANDLE_COCE_FAIL = 500;
    public static final int HANDLE_COCE_TIMEOUT = 502;

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
     * job log filename
     */
    private final String jobLogFileName;

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


    public XxlJobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HANDLE_COCE_SUCCESS;  // default success
    }

    public long getJobId() {
        return jobId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public String getJobLogFileName() {
        return jobLogFileName;
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

    private static InheritableThreadLocal<XxlJobContext> contextHolder = new InheritableThreadLocal<XxlJobContext>(); // support for child thread of job handler)

    public static void setXxlJobContext(XxlJobContext xxlJobContext){
        contextHolder.set(xxlJobContext);
    }

    public static XxlJobContext getXxlJobContext(){
        return contextHolder.get();
    }

}