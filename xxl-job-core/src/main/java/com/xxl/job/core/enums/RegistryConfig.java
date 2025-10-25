package com.xxl.job.core.enums;

/**
 * Created by xuxueli on 17/5/10.
 */
public class RegistryConfig {

    /**
     * registry beat interval, default 30s
     */
    public static final int BEAT_TIMEOUT = 30;

    /**
     * registry dead timeout, default 90s
     */
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    /**
     * registry type
     */
    public enum RegistType{ EXECUTOR, ADMIN }

}
