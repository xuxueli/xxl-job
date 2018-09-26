package com.xxl.job.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date util
 *
 * @author xuxueli 2018-08-19 01:24:11
 */
public class DateUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected synchronized SimpleDateFormat initialValue() {
            //return super.initialValue();
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };

    public static String format(Date date) {
        return threadLocal.get().format(date);
    }

    public static Date parse(String textDate) throws ParseException {
        return threadLocal.get().parse(textDate);
    }
}