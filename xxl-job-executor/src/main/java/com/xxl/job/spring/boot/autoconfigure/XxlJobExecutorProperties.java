package com.xxl.job.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * xxl-job执行器属性
 *
 * @author Rong.Jia
 * @date 2023/01/11
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobExecutorProperties {

    /**
     *  是否开启, 默认:false
     */
    private boolean enabled = Boolean.FALSE;

    /**
     * 调度平台
     */
    private JobAdmin admin = new JobAdmin();

    /**
     * 执行器
     */
    private JobExecutor executor = new JobExecutor();

    @Data
    public static class JobAdmin {

        /**
         * 调度中心部署跟地址 [必填]
         */
        private List<String> addresses;

    }

    @Data
    public static class JobExecutor {

        /**
         *  执行器AppName[必填]:执行器心跳注册分组依据
         */
        private String appName;

        /**
         *  执行器IP[选填]:默认为空表示自动获取IP,多网卡时可手动设置指定IP,该IP不会绑定Host仅作为通讯实用.
         *  地址信息用于'执行器注册'和'调度中心请求并触发任务'.
         */
        private String host;

        /**
         *  执行器端口号[选填],单机部署多个执行器时,注意要配置不同执行器端口.
         */
        private Integer port;

        /**
         *  执行器运行日志文件存储磁盘路径[选填]:需要对该路径拥有读写权限,为空则使用默认路径.
         */
        private String logPath = "logs/xxl-job/job-handler";

        /**
         *  执行器日志文件保存天数[选填],过期日志自动清理,
         *  限制值大于等于3时生效;否则,如-1,关闭自动清理功能. 默认: 7
         */
        private Integer logRetentionDays = 7;






    }

}
