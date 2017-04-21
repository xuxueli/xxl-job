package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.handler.IJobHandler;
import org.springframework.stereotype.Service;
import com.xxl.job.core.handler.annotation.JobHander;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 运行Linux Shell命令的任务处理器，注意shell命令以参数的形式传入到此处理器中。
 *
 * @author zhangqunshi@126.com
 */
@JobHander(value = "shellJobHandler")
@Service
public class ShellJobHandler extends IJobHandler {

    private static transient Logger logger
            = LoggerFactory.getLogger(ShellJobHandler.class);

    @Override
    public void execute(String... params) throws Exception {
        // 上层只传了一个参数，并没有分解 :(
        if (params != null && params.length > 0) {
            logger.info("Run command: " + Arrays.toString(params));
        } else {
            throw new Exception("Shell command is required!");
        }

        String[] cmd = params[0].split(" "); //使用空格把命令字符串分解
        Process process;
        process = Runtime.getRuntime().exec(cmd, null, null);

        InputStreamReader in = new InputStreamReader(process.getInputStream());
        LineNumberReader reader = new LineNumberReader(in);

        int exitCode = process.waitFor();
        logger.info("exit code: " + exitCode);

        String line;
        while ((line = reader.readLine()) != null) {
            logger.info(line);
        }
    }

}
