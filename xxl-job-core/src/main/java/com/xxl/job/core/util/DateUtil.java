package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * date util
 *
 * @author xuxueli 2018-08-19 01:24:11
 */
public class DateUtil {

    // ---------------------- format parse ----------------------
    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ThreadLocal<Map<String, DateFormat>> dateFormatThreadLocal = new ThreadLocal<Map<String, DateFormat>>();
    private static DateFormat getDateFormat(String pattern) {
        if (pattern==null || pattern.trim().length()==0) {
            throw new IllegalArgumentException("pattern cannot be empty.");
        }

        Map<String, DateFormat> dateFormatMap = dateFormatThreadLocal.get();
        if(dateFormatMap!=null && dateFormatMap.containsKey(pattern)){
            return dateFormatMap.get(pattern);
        }

        synchronized (dateFormatThreadLocal) {
            if (dateFormatMap == null) {
                dateFormatMap = new HashMap<String, DateFormat>();
            }
            dateFormatMap.put(pattern, new SimpleDateFormat(pattern));
            dateFormatThreadLocal.set(dateFormatMap);
        }

        return dateFormatMap.get(pattern);
    }

    /**
     * format datetime. like "yyyy-MM-dd"
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * format date. like "yyyy-MM-dd HH:mm:ss"
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String formatDateTime(Date date) {
        return format(date, DATETIME_FORMAT);
    }

    /**
     * format date
     *
     * @param date
     * @param patten
     * @return
     * @throws ParseException
     */
    public static String format(Date date, String patten) {
        return getDateFormat(patten).format(date);
    }

    /**
     * parse date string, like "yyyy-MM-dd HH:mm:s"
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateString){
        return parse(dateString, DATE_FORMAT);
    }

    /**
     * parse datetime string, like "yyyy-MM-dd HH:mm:ss"
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date parseDateTime(String dateString) {
        return parse(dateString, DATETIME_FORMAT);
    }

    /**
     * parse date
     *
     * @param dateString
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parse(String dateString, String pattern) {
        try {
            Date date = getDateFormat(pattern).parse(dateString);
            return date;
        } catch (Exception e) {
            logger.warn("parse date error, dateString = {}, pattern={}; errorMsg = {}", dateString, pattern, e.getMessage());
            return null;
        }
    }


    // ---------------------- add date ----------------------

    public static Date addYears(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

}