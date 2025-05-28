//package com.xxl.job.executor.utils;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.IoUtil;
//import cn.hutool.core.util.CharsetUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.ZipUtil;
//import com.xxl.job.core.constants.FileConstant;
//import com.xxl.job.core.constants.NumberConstant;
//import com.xxl.job.core.enums.KettleLogLevel;
//import com.xxl.job.executor.context.XxlJobHelper;
//import com.xxl.job.executor.exceptions.XxlJobExecutorException;
//import lombok.extern.slf4j.Slf4j;
//import org.pentaho.di.core.KettleEnvironment;
//import org.pentaho.di.core.logging.KettleLogStore;
//import org.pentaho.di.core.logging.LogLevel;
//import org.pentaho.di.core.logging.LoggingBuffer;
//import org.pentaho.di.core.plugins.PluginFolder;
//import org.pentaho.di.core.plugins.StepPluginType;
//import org.pentaho.di.core.util.EnvUtil;
//import org.pentaho.di.job.Job;
//import org.pentaho.di.job.JobEntryResult;
//import org.pentaho.di.job.JobMeta;
//import org.pentaho.di.trans.Trans;
//import org.pentaho.di.trans.TransMeta;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
///**
// * kettle工具类
// *
// * @author Rong.Jia
// * @date 2023/08/30
// */
//@Slf4j
//public class KettleUtils {
//
//    /**
//     * 执行ktr
//     *
//     * @param bytes  ktr文件字节
//     * @param name 任务名称
//     * @param params 参数
//     */
//    public static void callTrans(String name, byte[] bytes, Map<String, String> params) {
//        callTrans(name, bytes, params, KettleLogLevel.BASIC);
//    }
//
//    /**
//     * 执行ktr
//     *
//     * @param bytes  ktr文件字节
//     * @param name 任务名称
//     * @param logLevel 日志级别
//     */
//    public static void callTrans(String name, byte[] bytes, KettleLogLevel logLevel) {
//        callTrans(name, bytes, Collections.emptyMap(), logLevel);
//    }
//
//    /**
//     * 执行ktr
//     *
//     * @param bytes  ktr文件字节
//     * @param params 参数个数
//     * @param name 任务名称
//     * @param logLevel 日志级别
//     */
//    public static void callTrans(String name, byte[] bytes, Map<String, String> params, KettleLogLevel logLevel) {
//        InputStream in = null;
//        try {
//            initEnv();
//            in = new ByteArrayInputStream(bytes);
//            TransMeta transMeta = new TransMeta(in, null, Boolean.TRUE, null, null);
//            Trans trans = new Trans(transMeta);
//
//            if (CollectionUtil.isNotEmpty(params)) {
//                params.forEach(trans::setVariable);
//            }
//
//            trans.setLogLevel(LogLevel.valueOf(logLevel.name()));
//            trans.execute(null);
//            trans.waitUntilFinished();
//
//            LoggingBuffer appender = KettleLogStore.getAppender();
//
//            if (trans.getErrors() > 0) {
//                XxlJobHelper.log("There are errors during transformation exception");
//                if (trans.getResult().getNrErrors() > 0) {
//                    String errorText = appender.getBuffer(trans.getResult().getLogChannelId(), Boolean.TRUE).toString();
//                    XxlJobHelper.log("{}  \n {}", name, errorText);
//                }
//            }else {
//                String logChannelId = trans.getLogChannelId();
//                String logText = appender.getBuffer(logChannelId, true).toString();
//                XxlJobHelper.log("{}  \n {}",name, logText);
//            }
//            appender.clear();
//        }catch (Exception e) {
//            XxlJobHelper.log(e);
//        }finally {
//            IoUtil.close(in);
//        }
//    }
//
//    /**
//     * 执行kjb
//     *
//     * @param zip  kjb压缩包文件字节
//     * @param name 任务名称
//     * @param guideKjb kjb引导文件名
//     * @param params 参数
//     */
//    public static void callJob(String name, String guideKjb, byte[] zip, Map<String, String> params) {
//        callJob(name, guideKjb, zip, params, KettleLogLevel.BASIC);
//    }
//
//    /**
//     * 执行kjb
//     *
//     * @param zip  kjb压缩包文件字节
//     * @param name 任务名称
//     * @param guideKjb kjb引导文件名
//     * @param logLevel 日志级别
//     */
//    public static void callJob(String name, String guideKjb, byte[] zip, KettleLogLevel logLevel) {
//        callJob(name, guideKjb, zip, Collections.emptyMap(), logLevel);
//    }
//
//    /**
//     * 执行kjb
//     *
//     * @param zip  kjb文件字节
//     * @param params 参数个数
//     * @param guideKjb kjb引导文件名
//     * @param name 任务名称
//     * @param logLevel 日志级别
//     */
//    public static void callJob(String name, String guideKjb, byte[] zip, Map<String, String> params, KettleLogLevel logLevel) {
//        InputStream in = null;
//        File zipFile = null;
//        try {
//            in = new ByteArrayInputStream(zip);
//            zipFile = ZipUtil.unzip(in, new File(FileConstant.TMP_DIR + StrUtil.SLASH + IdUtil.fastSimpleUUID()), CharsetUtil.CHARSET_UTF_8);
//            List<File> kjb = FileUtil.loopFiles(zipFile, pathname -> pathname.isFile() && StrUtil.equals(guideKjb, pathname.getName()));
//
//            if (CollectionUtil.isEmpty(kjb)) {
//                XxlJobHelper.log("The kjb boot file [{}] does not exist in the kjb package", guideKjb);
//                return;
//            }
//
//            File file = kjb.get(NumberConstant.ZERO);
//            initEnv();
//            JobMeta jobMeta = new JobMeta(file.getPath(), null, null);
//            Job job = new Job(null, jobMeta);
//
//            if (CollectionUtil.isNotEmpty(params)) {
//                params.forEach(job::setVariable);
//            }
//
//            job.setLogLevel(LogLevel.valueOf(logLevel.name()));
//            job.start();
//            job.waitUntilFinished();
//
//            LoggingBuffer appender = KettleLogStore.getAppender();
//
//            if (job.getErrors() > 0) {
//                XxlJobHelper.log("There are errors during job exception");
//                List<JobEntryResult> jobEntryResults = job.getJobEntryResults();
//                jobEntryResults.forEach(jobEntryResult -> {
//                    if (jobEntryResult.getResult().getNrErrors() > 0) {
//                        String errorText = appender.getBuffer(jobEntryResult.getLogChannelId(), Boolean.TRUE).toString();
//                        XxlJobHelper.log("{} \r\n {}", name, errorText);
//                    }
//                });
//            }else {
//                String logChannelId = job.getLogChannelId();
//                String logText = appender.getBuffer(logChannelId, true).toString();
//                XxlJobHelper.log("{}  \n {}", name, logText);
//            }
//            appender.clear();
//        }catch (Exception e) {
//            log.error(e.getMessage(), e);
//            XxlJobHelper.log(e);
//        }finally {
//            IoUtil.close(in);
//            FileUtil.del(zipFile);
//        }
//    }
//
//    /**
//     * 初始化环境
//     */
//    private static void initEnv() {
//        try {
//            KettleEnvironment.init();
//            EnvUtil.environmentInit();
//        }catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw new XxlJobExecutorException(e);
//        }
//    }
//
//    /**
//     * 初始化环境
//     *
//     * @param pluginFolder 插件文件夹
//     */
//    private static void initEnv(String pluginFolder) {
//        try {
//            StepPluginType.getInstance().getPluginFolders().
//                    add(new PluginFolder(pluginFolder, false, true));
//            KettleEnvironment.init();
//            EnvUtil.environmentInit();
//        }catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw new XxlJobExecutorException(e);
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
