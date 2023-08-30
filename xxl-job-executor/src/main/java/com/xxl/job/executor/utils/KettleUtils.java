package com.xxl.job.executor.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import com.xxl.job.core.enums.KettleLogLevel;
import com.xxl.job.executor.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * kettle工具类
 *
 * @author Rong.Jia
 * @date 2023/08/30
 */
@Slf4j
public class KettleUtils {

    /**
     * 执行ktr
     *
     * @param bytes  ktr文件字节
     * @param name 名称
     * @param params 参数
     */
    public static void callTrans(String name, byte[] bytes, Map<String, String> params) {
        callTrans(name, bytes, params, KettleLogLevel.BASIC);
    }

    /**
     * 执行ktr
     *
     * @param bytes  ktr文件字节
     * @param name 名称
     * @param logLevel 日志级别
     */
    public static void callTrans(String name, byte[] bytes, KettleLogLevel logLevel) {
        callTrans(name, bytes, Collections.emptyMap(), logLevel);
    }

    /**
     * 执行ktr
     *
     * @param bytes  ktr文件字节
     * @param params 参数个数
     * @param name 名称
     * @param logLevel 日志级别
     */
    public static void callTrans(String name, byte[] bytes, Map<String, String> params, KettleLogLevel logLevel) {
        InputStream in = null;
        try {
            KettleEnvironment.init();
            EnvUtil.environmentInit();
            in = new ByteArrayInputStream(bytes);
            TransMeta transMeta = new TransMeta(in, null, Boolean.TRUE, null, null);
            Trans trans = new Trans(transMeta);

            if (CollectionUtil.isNotEmpty(params)) {
                params.forEach(trans::setVariable);
            }

            trans.setLogLevel(LogLevel.valueOf(logLevel.name()));
            trans.execute(null);
            trans.waitUntilFinished();

            String logChannelId = trans.getLogChannelId();
            LoggingBuffer appender = KettleLogStore.getAppender();
            String logText = appender.getBuffer(logChannelId, true).toString();
            XxlJobHelper.log("{} : {}",name, logText);
            if (trans.getErrors() > 0) {
                String msg = "There are errors during transformation exception!(转换过程中发生异常)";
                XxlJobHelper.log(msg);
            }
        }catch (Exception e) {
            XxlJobHelper.log(e);
        }finally {
            IoUtil.close(in);
        }
    }














}
