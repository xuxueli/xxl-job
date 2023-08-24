package com.xxl.job.admin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * xxl-job管理属性
 *
 * @author Rong.Jia
 * @date 2023/05/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "xxl.job.admin")
public class XxlJobAdminProperties {

    private Integer triggerPoolFastMax = 200;

    private Integer triggerPoolSlowMax = 100;

    private Integer logRetentionDay = 7;

    public Integer getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public Integer getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public Integer getLogRetentionDay() {
        if (logRetentionDay < 7) {
            return -1;
        }
        return logRetentionDay;
    }



}
