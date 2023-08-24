package com.xxl.job.admin.common.utils;

import cn.hutool.core.convert.Convert;
import com.xxl.job.admin.common.enums.ScheduleTypeEnum;
import com.xxl.job.core.utils.CronExpression;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * cron工具类
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Slf4j
public class CronUtils {

    /**
     * 生成下一个有效时间
     *
     * @param scheduleConf 调度配置
     * @param scheduleType 调度类型 {@link ScheduleTypeEnum}
     * @param fromTime 从时间
     * @return {@link Date}
     * @throws Exception 生成异常
     */
    public static Date generateNextValidTime(String scheduleType, String scheduleConf, Date fromTime) {
        try {
            ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(scheduleType);
            if (ScheduleTypeEnum.CRON.equals(scheduleTypeEnum)) {
                return new CronExpression(scheduleConf).getNextValidTimeAfter(fromTime);
            } else if (ScheduleTypeEnum.FIX_RATE.equals(scheduleTypeEnum)) {
                return new Date(fromTime.getTime() + Convert.toLong(scheduleConf) * 1000);
            }
        }catch (Exception e) {
            log.error("generateNextValidTime {}", e.getMessage());
        }

        return null;
    }


}
