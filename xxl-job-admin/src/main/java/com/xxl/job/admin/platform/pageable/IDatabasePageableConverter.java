package com.xxl.job.admin.platform.pageable;

/**
 * @author Ice2Faith
 * @date 2024/7/5 11:11
 * @desc
 */
public interface IDatabasePageableConverter {
    boolean supportDatabase(String type);
    DatabasePageable converter(int start,int length);
}
