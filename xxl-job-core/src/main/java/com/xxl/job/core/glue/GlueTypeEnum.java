package com.xxl.job.core.glue;

/**
 * Created by xuxueli on 17/4/26.
 */
public enum GlueTypeEnum {

    BEAN("BEAN模式", false, null, null),
    GLUE_GROOVY("GLUE模式(Java)", false, null, null),
    GLUE_SHELL("GLUE模式(Shell)", true, "bash", ".sh"),
    GLUE_PYTHON("GLUE模式(Python)", true, "python", ".py"),
    GLUE_NODEJS("GLUE模式(Nodejs)", true, "node", ".js");

    private String desc;
    private boolean isScript;
    private String cmd;
    private String suffix;

    private GlueTypeEnum(String desc, boolean isScript, String cmd, String suffix) {
        this.desc = desc;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isScript() {
        return isScript;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSuffix() {
        return suffix;
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
