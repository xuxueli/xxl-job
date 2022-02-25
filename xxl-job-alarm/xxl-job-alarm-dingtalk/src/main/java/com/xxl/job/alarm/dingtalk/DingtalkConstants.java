package com.xxl.job.alarm.dingtalk;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
public final class DingtalkConstants {

    static final String DINGTALK_WEBHOOK = "alarm.dingtalk.webhook";

    static final String DINGTALK_KEYWORD = "alarm.dingtalk.keyword";

    static final String DINGTALK_SECRET = "alarm.dingtalk.secret";

    static final String DINGTALK_AT_MOBILES = "alarm.dingtalk.at-mobiles";

    static final String DINGTALK_AT_USERIDS = "alarm.dingtalk.at-userIds";

    static final String DINGTALK_AT_ALL = "alarm.dingtalk.at-all";

    private DingtalkConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
