package com.xxl.job.core.handler.impl;

import java.io.File;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
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

        // clean old script file
        File glueSrcPath = new File(XxlJobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList != null) {
                for (File glueSrcFileItem : glueSrcFileList) {
                    if (glueSrcFileItem.getName().startsWith(jobId +"_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {

        if (!glueType.isScript()) {
            XxlJobHelper.handleFail("glueType["+ glueType +"] invalid.");
            return;
        }

        // cmd
        String cmd = glueType.getCmd();

	    // make script file
	    String scriptFileName = XxlJobFileAppender.getGlueSrcPath()
			    + File.separator
			    + jobId
			    + "_"
			    + glueUpdatetime
			    + glueType.getSuffix(); // 提高字符串拼接性能
	    File scriptFile = new File(scriptFileName);
	    if (!scriptFile.exists()) {
		    ScriptUtil.markScriptFile(scriptFileName, gluesource);
	    }

	    // log file
	    final XxlJobContext context = XxlJobContext.getXxlJobContext();
	    String logFileName = context.getJobLogFileName();

	    // script params：0=param、1=分片序号、2=分片总数
	    String[] scriptParams = new String[3];
	    scriptParams[0] = XxlJobHelper.getJobParam();
	    scriptParams[1] = String.valueOf(context.getShardIndex());
	    scriptParams[2] = String.valueOf(context.getShardTotal());

        // invoke
        XxlJobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            XxlJobHelper.handleSuccess();
        } else {
            XxlJobHelper.handleFail("script exit value("+exitValue+") is failed");
        }

    }

}
