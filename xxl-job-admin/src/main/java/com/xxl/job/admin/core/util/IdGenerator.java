package com.xxl.job.admin.core.util;

/**
 * @author 王政
 * @date 2022/5/18 15:54
 */
public class IdGenerator {

    private static final Sequence WORKER = new Sequence();

    private IdGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

}
