package com.xxl.job.executor.factory.handler;

import cn.hutool.core.util.ArrayUtil;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.executor.context.XxlJobContext;
import com.xxl.job.executor.context.XxlJobHelper;
import com.xxl.job.executor.utils.JobLogUtils;
import com.xxl.job.executor.utils.ScriptUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

/**
 * 脚本处理程序
 * @author Rong.Jia
 * @date 2023/05/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScriptJobHandler extends JobHandler {

    private Long jobId;
    private Long glueUpdateTime;
    private String glueSource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(Long jobId, Long glueUpdateTime, String glueSource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdateTime = glueUpdateTime;
        this.glueSource = glueSource;
        this.glueType = glueType;

        // clean old script file
        File glueSrcPath = new File(JobLogUtils.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFiles = glueSrcPath.listFiles();
            if (ArrayUtil.isNotEmpty(glueSrcFiles)) {
                for (File glueSrcFileItem : glueSrcFiles) {
                    if (glueSrcFileItem.getName().startsWith(jobId +"_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    @Override
    public void execute(Object param) throws Exception {

        if (!glueType.isScript()) {
            XxlJobHelper.handleFail("glueType["+ glueType +"] invalid.");
            return;
        }

        // cmd
        String cmd = glueType.getCmd();

        // make script file
        String scriptFileName = JobLogUtils.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdateTime))
                .concat(glueType.getSuffix());
        File scriptFile = new File(scriptFileName);
        if (!scriptFile.exists()) {
            ScriptUtils.markScriptFile(scriptFileName, glueSource);
        }

        // log file
        String logFileName = XxlJobContext.getJobContext().getJobLogFileName();

        // script params：0=param、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        scriptParams[0] = XxlJobHelper.getJobParam();
        scriptParams[1] = String.valueOf(XxlJobContext.getJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(XxlJobContext.getJobContext().getShardTotal());

        // invoke
        XxlJobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtils.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            XxlJobHelper.handleSuccess();
        } else {
            XxlJobHelper.handleFail("script exit value("+exitValue+") is failed");
        }

    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

}
