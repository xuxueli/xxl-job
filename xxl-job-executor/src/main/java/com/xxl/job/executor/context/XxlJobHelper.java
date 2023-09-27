package com.xxl.job.executor.context;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.executor.utils.JobLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * xxl工作助手
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class XxlJobHelper {

    /**
     * 获取当前任务id
     *
     * @return long
     */
    public static Long getJobId() {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext)) return null;
        return xxlJobContext.getJobId();
    }

    /**
     * 获取当前任务参数
     *
     * @return {@link String}
     */
    public static String getJobParam() {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext)) return null;
        return xxlJobContext.getJobParam();
    }

    /**
     * 获取当前任务参数
     * @param tClass 参数类型
     * @return {@link String}
     */
    public static Object getJobParam(Class<?> tClass) {
        String jobParam = getJobParam();
        if (StrUtil.isBlank(jobParam)) return null;
        if (NumberUtil.isNumber(jobParam)) {
            return Convert.convert(tClass, jobParam);
        }else {
            if (JSONValidator.from(jobParam).validate()) {
                return JSONObject.parseObject(jobParam, tClass);
            }
        }
        return Convert.convert(tClass, jobParam);
    }

    /**
     * 获取当前任务参数
     * @param param 参数
     * @param tClass 参数类型
     * @return {@link String}
     */
    public static Object getJobParam(Object param, Class<?> tClass) {
        if (ObjectUtil.isEmpty(param)) return null;
        String jobParam = StrUtil.toString(param);
        if (NumberUtil.isNumber(jobParam)) {
            return Convert.convert(tClass, param);
        }else {
            if (JSONValidator.from(jobParam).validate()) {
                return JSONObject.parseObject(jobParam, tClass);
            }
        }
        return Convert.convert(tClass, jobParam);
    }

    /**
     * 获取当前作日志文件名字
     *
     * @return {@link String}
     */
    public static String getJobLogFileName() {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext)) return null;
        return xxlJobContext.getJobLogFileName();
    }

    /**
     * current ShardIndex
     *
     * @return
     */
    public static int getShardIndex() {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getShardIndex();
    }

    /**
     * current ShardTotal
     *
     * @return
     */
    public static Integer getShardTotal() {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext)) return -1;
        return xxlJobContext.getShardTotal();
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean log(String appendLogPattern, Object ... appendLogArguments) {

        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * append exception stack
     *
     * @param e
     */
    public static boolean log(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
    private static boolean logDetail(StackTraceElement callInfo, String appendLog) {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext))  return false;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DateUtil.formatDateTime(new Date())).append(" ")
                .append("["+ callInfo.getClassName() + "#" + callInfo.getMethodName() +"]").append("-")
                .append("["+ callInfo.getLineNumber() +"]").append("-")
                .append("["+ Thread.currentThread().getName() +"]").append(" ")
                .append(appendLog!=null?appendLog:"");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        String logFileName = xxlJobContext.getJobLogFileName();

        if (StrUtil.isNotBlank(logFileName)) {
            JobLogUtils.appendLog(logFileName, formatAppendLog);
            return true;
        } else {
            log.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }

    /**
     * 处理成功
     * @return boolean
     */
    public static boolean handleSuccess(){
        return handleResult(ResponseEnum.SUCCESS.getCode(), "处理成功");
    }

    /**
     * 处理成功
     *
     * @param message 消息
     * @return boolean
     */
    public static boolean handleSuccess(String message) {
        return handleResult(ResponseEnum.SUCCESS.getCode(), message);
    }

    /**
     * 处理失败
     *
     * @return boolean
     */
    public static boolean handleFail(){
        return handleResult(ResponseEnum.ERROR.getCode(), "处理异常, 请排查");
    }

    /**
     * 处理失败
     *
     * @param message 消息
     * @return boolean
     */
    public static boolean handleFail(String message) {
        return handleResult(ResponseEnum.ERROR.getCode(), message);
    }

    /**
     * 处理超时
     *
     * @return boolean
     */
    public static boolean handleTimeout(){
        return handleResult(ResponseEnum.TIMEOUT.getCode(), "调用超时");
    }

    /**
     * 处理超时
     *
     * @param message 消息
     * @return boolean
     */
    public static boolean handleTimeout(String message){
        return handleResult(ResponseEnum.TIMEOUT.getCode(), message);
    }

    /**
     * 处理结果
     *
     * @param handleCode 0 : success
     *                   500 : fail
     *                   502 : timeout
     * @param message  异常信息
     * @return boolean
     */
    public static boolean handleResult(int handleCode, String message) {
        XxlJobContext xxlJobContext = XxlJobContext.getJobContext();
        if (ObjectUtil.isEmpty(xxlJobContext)) return false;
        xxlJobContext.setHandleCode(handleCode);
        if (ObjectUtil.isNotNull(message)) xxlJobContext.setHandleMessage(message);
        return true;
    }


}
