package com.xxl.job.core.enums;

/**
 * Created by xuxueli on 17/5/10.
 */
public class RegistryConfig {
     //30秒重新注册一次
    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    public enum RegistType{ EXECUTOR, ADMIN }

}
