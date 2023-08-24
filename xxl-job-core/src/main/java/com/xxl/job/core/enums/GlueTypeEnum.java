package com.xxl.job.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 运行模式
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Getter
@AllArgsConstructor
public enum GlueTypeEnum {

    // NULL
    NULL("", false, null, null),

    // BEAN
    BEAN("BEAN", false, null, null),

    // Java-src
    GLUE_GROOVY_SRC("GLUE(Java-src)", false, null, null),

    // Java-class
    GLUE_GROOVY_CLASS("GLUE(Java-class)", false, null, null),

    // Shell
    GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),

    // Python
    GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),

    // PHP
    GLUE_PHP("GLUE(PHP)", true, "php", ".php"),

    // Nodejs
    GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),

    // PowerShell
    GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell", ".ps1"),

    // kettle
    KETTLE("kettle", false, null, ".ktr"),










    ;

    private final String desc;
    private final boolean isScript;
    private final String cmd;
    private final String suffix;

    public static GlueTypeEnum match(String name) {
        return Arrays.stream(GlueTypeEnum.values())
                .filter(a -> a.name().equalsIgnoreCase(name))
                .findAny().orElse(NULL);
    }



}
