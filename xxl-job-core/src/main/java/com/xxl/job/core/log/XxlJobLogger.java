package com.xxl.job.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuxueli on 17/4/28.
 */
public class XxlJobLogger {
    private static Logger logger = LoggerFactory.getLogger("xxl-job logger");
    private static SimpleDateFormat xxlJobLoggerFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // TODO，concurrent issue

    /**
     * append log
     *
     * @param appendLog
     */
    public static void log(String appendLog) {

        // logFileName
        String logFileName = XxlJobFileAppender.contextHolder.get();
        if (logFileName==null || logFileName.trim().length()==0) {
            return;
        }

        // "yyyy-MM-dd HH:mm:ss [ClassName]-[MethodName]-[LineNumber]-[ThreadName] log";
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[1];

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(xxlJobLoggerFormat.format(new Date())).append(" ")
            .append("["+ callInfo.getClassName() +"]").append("-")
            .append("["+ callInfo.getMethodName() +"]").append("-")
            .append("["+ callInfo.getLineNumber() +"]").append("-")
            .append("["+ Thread.currentThread().getName() +"]").append(" ")
            .append(appendLog!=null?appendLog:"");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        XxlJobFileAppender.appendLog(logFileName, formatAppendLog);

        logger.debug(">>>>>>>>>>> [{}]: {}", logFileName, formatAppendLog);
    }

    /**
     * append log with pattern
     *
     * @
     *
     * @param appendLogPattern  like "aaa {0} bbb {1} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static void log(String appendLogPattern, Object ... appendLogArguments) {
        String appendLog = MessageFormat.format(appendLogPattern, appendLogArguments);
        log(appendLog);
    }

}
