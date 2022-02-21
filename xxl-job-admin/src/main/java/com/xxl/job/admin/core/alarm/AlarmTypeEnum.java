package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * @author tomj2ee 2022-02-20 11:11:23
 */
public enum AlarmTypeEnum {
    /**
     * 邮件
     */
    EMAIL(1,I18nUtil.getString("jobinfo_field_email")),

    /**
     * webhook
     */
    WEBHOOK(2,I18nUtil.getString("jobinfo_field_webhook"));



    private String title;
    private int alarmType;

    AlarmTypeEnum(int alarmType,String title) {
        this.alarmType=alarmType;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getAlarmType() {
        return alarmType;
    }


    public static AlarmTypeEnum match(String name, AlarmTypeEnum defaultItem){
        for (AlarmTypeEnum item: AlarmTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
