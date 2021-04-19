package com.xxl.job.core.glue;

/**
 * Created by xuxueli on 17/4/26.  运行模式
 */
public enum GlueTypeEnum {

    BEAN("BEAN", false, null, null), //任务以JobHandler方式维护在执行器端；需要结合 "JobHandler" 属性匹配执行器中任务；
    GLUE_GROOVY("GLUE(Java)", false, null, null),//任务以源码方式维护在调度中心；该模式的任务实际上是一段继承自IJobHandler的Java类代码并 "groovy" 源码方式维护，它在执行器项目中运行，可使用@Resource/@Autowire注入执行器里中的其他服务；
    GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),//任务以源码方式维护在调度中心；该模式的任务实际上是一段 "shell" 脚本；
    GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),  //任务以源码方式维护在调度中心；该模式的任务实际上是一段 "python" 脚本；
    GLUE_PHP("GLUE(PHP)", true, "php", ".php"), //任务以源码方式维护在调度中心；该模式的任务实际上是一段 "php" 脚本；
    GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),  //任务以源码方式维护在调度中心；该模式的任务实际上是一段 "nodejs" 脚本；
    GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell", ".ps1");    //任务以源码方式维护在调度中心；该模式的任务实际上是一段 "PowerShell" 脚本；

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
