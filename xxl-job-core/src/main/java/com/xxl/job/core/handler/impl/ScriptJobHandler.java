package com.xxl.job.core.handler.impl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ScriptUtil;

/**
 * Created by xuxueli on 17/4/27.
 */
public class ScriptJobHandler extends IJobHandler {

    private int jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;
    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public ReturnT<String> execute(String... params) throws Exception {

        // cmd + script-file-name
        String cmd = "bash";
        String scriptFileName = null;
        if (GlueTypeEnum.GLUE_SHELL == glueType) {
            cmd = "bash";
            scriptFileName = XxlJobFileAppender.logPath.concat("gluesource/").concat(String.valueOf(jobId)).concat("_").concat(String.valueOf(glueUpdatetime)).concat(".sh");
        } else if (GlueTypeEnum.GLUE_PYTHON == glueType) {
            cmd = "python";
            scriptFileName = XxlJobFileAppender.logPath.concat("gluesource/").concat(String.valueOf(jobId)).concat("_").concat(String.valueOf(glueUpdatetime)).concat(".py");
        } else if (GlueTypeEnum.GLUE_NODEJS == glueType) {
            cmd = "node";
            scriptFileName = XxlJobFileAppender.logPath.concat("gluesource/").concat(String.valueOf(jobId)).concat("_").concat(String.valueOf(glueUpdatetime)).concat(".js");
        }

        // make script file
        ScriptUtil.markScriptFile(scriptFileName, gluesource);

        // log file
        String logFileName = XxlJobFileAppender.logPath.concat(XxlJobFileAppender.contextHolder.get());

        // invoke
        XxlJobLogger.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, params);
        ReturnT<String> result = (exitValue==0)?ReturnT.SUCCESS:new ReturnT<String>(ReturnT.FAIL_CODE, "script exit value("+exitValue+") is failed");
        return result;
    }

}
