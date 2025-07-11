package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * @author tomj2ee 2022-02-20 11:11:23
 */
public enum AlarmTypeEnum {
    /**
     * 不报警
     */
    NOT(0,"不报警"),

    /**
     * 邮件
     */
    EMAIL(1,I18nUtil.getString("jobinfo_field_email")),


    /**
     * 企业微信
     */
    ENT_WECHAT(2,I18nUtil.getString("jobinfo_field_entwechat")),

    /**
     * 飞书
     */
    FEI_SHU(3,I18nUtil.getString("jobinfo_field_feishu")),

    /**
     * 叮叮
     */
    DING_DING(4,I18nUtil.getString("jobinfo_field_dingding")),

    /**
     * webhook
     */
    WEBHOOK(5,I18nUtil.getString("jobinfo_field_webhook"));



    private String title;
    private int alarmType;

    AlarmTypeEnum(int alarmType, String title) {
        this.alarmType=alarmType;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
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
