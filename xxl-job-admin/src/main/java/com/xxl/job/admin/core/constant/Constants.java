package com.xxl.job.admin.core.constant;

/**
 * job group
 * 
 * @author xuxueli 2016-1-15 14:23:05
 */
public class Constants {

    public enum JobGroupEnum {
        DEFAULT("默认"),
        MEMBER("会员"),
        ORDER("订单"),
        ITEM("商品"),
        PAY("支付"),
        MSG("消息"),
        SEARCH("搜索");
        private String desc;

        private JobGroupEnum(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public static JobGroupEnum match(String name) {
            if (name == null || name.trim().length() == 0) {
                return null;
            }
            for (JobGroupEnum group : JobGroupEnum.values()) {
                if (group.name().equals(name)) {
                    return group;
                }
            }
            return null;
        }
    }
}
