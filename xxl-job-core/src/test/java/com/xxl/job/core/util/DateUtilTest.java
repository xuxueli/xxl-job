package com.xxl.job.core.util;

import java.util.Date;

public class DateUtilTest {
    public static void main(String[] args) {
        long timeStamp = 1677130058000L;
        Date date = DateUtil.timeStampToDate(timeStamp, "");
        System.out.println(date);
        System.out.println(new Date());
    }
}
