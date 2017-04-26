package com.xxl.job.core.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.FileOutputStream;

/**
 *  1、内嵌编译器如"PythonInterpreter"无法引用扩展包，因此推荐使用java调用控制台进程方式"Runtime.getRuntime().exec()"来运行脚本(shell或python)；
 *  2、因为通过java调用控制台进程方式实现，需要保证目标机器PATH路径正确配置对应编译器；
 *  3、暂时脚本执行日志只能在脚本执行结束后一次性获取，无法保证实时性；因此为确保日志实时性，可改为将脚本打印的日志存储在指定的日志文件上；
 *
 *  知识点：
 *      1、日志输出到日志文件：[>>logfile 2>&1]：将错误输出2以及标准输出1都一起以附加写方式导入logfile文件
 *      2、python 异常输出优先级高于标准输出，体现在Log文件中，因此推荐通过logging方式打日志保持和异常信息一致；否则用prinf日志顺序会错乱
 *
 * Created by xuxueli on 17/2/25.
 */
public class ScriptUtil {

    private static String pyCmd = "python";
    private static String shllCmd = "bash";
    private static String pyFile = "/Users/xuxueli/workspaces/idea-git-workspace/github/xxl-incubator/xxl-util/src/main/resources/script/pytest.py";
    private static String shellFile = "/Users/xuxueli/workspaces/idea-git-workspace/github/xxl-incubator/xxl-util/src/main/resources/script/shelltest.sh";
    private static String pyLogFile = "/Users/xuxueli/Downloads/tmp/pylog.log";
    private static String shLogFile = "/Users/xuxueli/Downloads/tmp/shlog.log";

    public static void main(String[] args) {

        String command = pyCmd;
        String filename = pyFile;
        String logFile = pyLogFile;
        if (false) {
            command = shllCmd;
            filename = shellFile;
            logFile = shLogFile;
        }

        execToFile(command, filename, logFile);

    }

    public static File markScriptFile(){
        return null;
    }

    /**
     * 日志文件输出方式
     *
     * 优点：支持将目标数据实时输出到指定日志文件中去
     * 缺点：
     *      标准输出和错误输出优先级固定，可能和脚本中顺序不一致
     *      Java无法实时获取
     *
     * @param command
     * @param scriptFile
     * @param logFile
     */
    public static void execToFile(String command, String scriptFile, String logFile){
        try {
            // 标准输出：print （null if watchdog timeout）
            // 错误输出：logging + 异常 （still exists if watchdog timeout）
            // 标准输出
            FileOutputStream fileOutputStream = new FileOutputStream(logFile);
            PumpStreamHandler streamHandler = new PumpStreamHandler(fileOutputStream, fileOutputStream, null);

            // command
            CommandLine commandline = new CommandLine(command);
            commandline.addArgument(scriptFile);

            // exec
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            exec.setStreamHandler(streamHandler);
            int exitValue = exec.execute(commandline);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Process process = Runtime.getRuntime().exec(cmdarray);
        IOUtils.copy(process.getInputStream(), out);
        IOUtils.copy(process.getErrorStream(), out);*/
    }

}
