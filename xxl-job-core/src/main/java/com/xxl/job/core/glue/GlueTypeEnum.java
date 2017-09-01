package com.xxl.job.core.glue;

/**
 * Created by xuxueli on 17/4/26.
 */
public enum GlueTypeEnum {

    BEAN("BEAN模式"),
    GLUE_GROOVY("GLUE模式(Java)"),
    GLUE_SHELL("GLUE模式(Shell)"),
    GLUE_PYTHON("GLUE模式(Python)"),
    GLUE_NODEJS("GLUE模式(Nodejs)");

    private String desc;
    private GlueTypeEnum(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public static GlueTypeEnum match(String name){
        for (GlueTypeEnum item: GlueTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
