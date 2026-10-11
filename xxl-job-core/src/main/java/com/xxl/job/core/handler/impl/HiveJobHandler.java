package com.xxl.job.core.handler.impl;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.util.ScriptUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * GLUE(HiveSQL) 任务处理器：通过本机 Hive 安装目录下的 beeline 客户端连接 HiveServer2 执行 Hive SQL。
 *
 * 连接配置（优先级：JVM 系统属性 > 环境变量 > 默认值，默认适配本机 HiveServer2）：
 *     xxl.job.hive.home / XXL_JOB_HIVE_HOME        Hive 安装目录，未配置时读取环境变量 HIVE_HOME，仍不存在则直接使用 PATH 中的 beeline；
 *     xxl.job.hive.jdbc-url / XXL_JOB_HIVE_JDBC_URL    HiveServer2 JDBC 地址，默认 jdbc:hive2://127.0.0.1:10000/default；
 *     xxl.job.hive.user / XXL_JOB_HIVE_USER        HiveServer2 用户名，默认空（HiveServer2 未开启认证时使用本机用户）；
 *     xxl.job.hive.password / XXL_JOB_HIVE_PASSWORD    HiveServer2 密码，默认空。
 *
 * SQL 参数：任务入参、分片序号、分片总数会以 hivevar 注入，SQL 中可通过 ${jobParam}、${shardIndex}、${shardTotal} 引用；
 * 例如：select * from demo where dt = '${jobParam}';
 *
 * Created by jzy67 on 26/8/16.
 */
public class HiveJobHandler extends IJobHandler {

    // 配置项（JVM 系统属性）
    private static final String CONFIG_HIVE_HOME = "xxl.job.hive.home";
    private static final String CONFIG_HIVE_JDBC_URL = "xxl.job.hive.jdbc-url";
    private static final String CONFIG_HIVE_USER = "xxl.job.hive.user";
    private static final String CONFIG_HIVE_PASSWORD = "xxl.job.hive.password";
    private static final String ENV_HIVE_JDBC_URL = "XXL_JOB_HIVE_JDBC_URL";
    private static final String ENV_HIVE_USER = "XXL_JOB_HIVE_USER";
    private static final String ENV_HIVE_PASSWORD = "XXL_JOB_HIVE_PASSWORD";

    // 默认 HiveServer2 JDBC 地址（本机 HiveServer2 默认端口 10000）
    private static final String DEFAULT_HIVE_JDBC_URL = "jdbc:hive2://127.0.0.1:10000/default";

    private final int jobId;
    private final long glueUpdatetime;
    private final String gluesource;

    public HiveJobHandler(int jobId, long glueUpdatetime, String gluesource){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;

        // clean old sql file
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

        // 1、resolve beeline + connection config
        String beeline = resolveBeeline();
        String jdbcUrl = resolveConfig(CONFIG_HIVE_JDBC_URL, ENV_HIVE_JDBC_URL, DEFAULT_HIVE_JDBC_URL);
        String user = resolveConfig(CONFIG_HIVE_USER, ENV_HIVE_USER, "");
        String password = resolveConfig(CONFIG_HIVE_PASSWORD, ENV_HIVE_PASSWORD, "");

        if (!resolveBeelineExecutable(beeline)) {
            XxlJobHelper.log("----------- beeline not found, please check xxl.job.hive.home / HIVE_HOME / PATH config. beeline:" + beeline + " -----------");
            XxlJobHelper.handleFail("hive beeline not found, please check xxl.job.hive.home / HIVE_HOME / PATH config. beeline:" + beeline);
            return;
        }

        // 2、make sql file
        String sqlFileName = XxlJobFileAppender.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(".sql");
        File sqlFile = new File(sqlFileName);
        if (!sqlFile.exists()) {
            ScriptUtil.markScriptFile(sqlFileName, gluesource);
        }

        // 3、build beeline command
        List<String> cmdarray = new ArrayList<>();
        cmdarray.add(beeline);
        cmdarray.add("-u");
        cmdarray.add(jdbcUrl);
        if (user != null && !user.trim().isEmpty()) {
            cmdarray.add("-n");
            cmdarray.add(user);
            cmdarray.add("-p");
            cmdarray.add(password != null ? password : "");
        }

        // 4、hivevar：任务入参 + 分片参数，SQL 中通过 ${jobParam}/${shardIndex}/${shardTotal} 引用
        String jobParam = XxlJobHelper.getJobParam();
        cmdarray.add("--hivevar");
        cmdarray.add("jobParam=" + (jobParam != null ? jobParam : ""));
        cmdarray.add("--hivevar");
        cmdarray.add("shardIndex=" + XxlJobContext.getXxlJobContext().getShardIndex());
        cmdarray.add("--hivevar");
        cmdarray.add("shardTotal=" + XxlJobContext.getXxlJobContext().getShardTotal());

        cmdarray.add("-f");
        cmdarray.add(sqlFileName);

        // 5、log file
        String logFileName = XxlJobContext.getXxlJobContext().getLogFileName();

        // 6、invoke
        XxlJobHelper.log("----------- beeline:" + beeline + " -----------");
        XxlJobHelper.log("----------- hive jdbc url:" + jdbcUrl + (user!=null && !user.trim().isEmpty() ? ", user:" + user : "") + " -----------");
        XxlJobHelper.log("----------- hive sql file:" + sqlFileName + " -----------");
        int exitValue = ScriptUtil.execToFile(cmdarray, logFileName);

        if (exitValue == 0) {
            XxlJobHelper.handleSuccess();
            return;
        } else {
            XxlJobHelper.handleFail("beeline exit value("+exitValue+") is failed");
            return;
        }
    }

    /**
     * resolve beeline binary：
     * 1、优先 xxl.job.hive.home 系统属性：${hive.home}/bin/beeline
     * 2、其次环境变量 HIVE_HOME：${HIVE_HOME}/bin/beeline
     * 3、最后直接使用 PATH 中的 beeline
     */
    private static String resolveBeeline() {
        String hiveHome = resolveConfig(CONFIG_HIVE_HOME, "XXL_JOB_HIVE_HOME", null);
        if (hiveHome != null && !hiveHome.trim().isEmpty()) {
            return hiveHome + File.separator + "bin" + File.separator + "beeline";
        }
        // 兜底：环境变量 HIVE_HOME
        String envHiveHome = System.getenv("HIVE_HOME");
        if (envHiveHome != null && !envHiveHome.trim().isEmpty()) {
            return envHiveHome + File.separator + "bin" + File.separator + "beeline";
        }
        return "beeline";
    }

    /**
     * 校验 beeline 是否可执行：
     * 1、绝对路径：文件存在即可；
     * 2、相对路径（PATH 查找）：PATH 中存在可执行的 beeline 文件。
     */
    private static boolean resolveBeelineExecutable(String beeline) {
        File beelineFile = new File(beeline);
        if (beelineFile.isAbsolute()) {
            return beelineFile.isFile() && beelineFile.canExecute();
        }
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            for (String pathItem : pathEnv.split(File.pathSeparator)) {
                if (pathItem == null || pathItem.trim().isEmpty()) {
                    continue;
                }
                File candidate = new File(pathItem, beeline);
                if (candidate.isFile() && candidate.canExecute()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * resolve config：系统属性 > 环境变量 > 默认值
     */
    private static String resolveConfig(String propertyKey, String envKey, String defaultValue) {
        String value = System.getProperty(propertyKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(envKey);
        }
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }

}
