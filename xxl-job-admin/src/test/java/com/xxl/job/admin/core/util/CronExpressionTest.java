package com.xxl.job.admin.core.util;

import com.xxl.job.admin.scheduler.cron.CronExpression;
import com.xxl.job.core.util.DateUtil;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CronExpressionTest {

    @Test
    public void shouldWriteValueAsString() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2025-06-29 00:00:00",
                2, "2025-07-06 00:00:00",
                3, "2025-07-13 00:00:00",
                4, "2025-07-20 00:00:00",
                5, "2025-07-27 00:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 0 ? * 1");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 28, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 每周最后一天
     */
    @Test
    public void testLastDayOfWeek() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2025-01-04 00:00:00");
                put(2, "2025-01-11 00:00:00");
                put(3, "2025-01-18 00:00:00");
                put(4, "2025-01-25 00:00:00");
                put(5, "2025-02-01 00:00:00");
            }
        };
        CronExpression cronExpression = new CronExpression("0 0 0 ? * L");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 月末最后一天
     */
    @Test
    public void testLastDayOfMonth() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2025-01-31 00:00:00");
                put(2, "2025-02-28 00:00:00");
                put(3, "2025-03-31 00:00:00");
                put(4, "2025-04-30 00:00:00");
                put(5, "2025-05-31 00:00:00");
                put(6, "2025-06-30 00:00:00");
                put(7, "2025-07-31 00:00:00");
                put(8, "2025-08-31 00:00:00");
                put(9, "2025-09-30 00:00:00");
                put(10, "2025-10-31 00:00:00");
                put(11, "2025-11-30 00:00:00");
                put(12, "2025-12-31 00:00:00");
            }
        };

        CronExpression cronExpression = new CronExpression("0 0 0 L * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * L-n
     */
    @Test
    public void testSecondLastDayOfMonth() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2025-01-30 00:00:00");
                put(2, "2025-02-27 00:00:00");
                put(3, "2025-03-30 00:00:00");
                put(4, "2025-04-29 00:00:00");
                put(5, "2025-05-30 00:00:00");
                put(6, "2025-06-29 00:00:00");
                put(7, "2025-07-30 00:00:00");
                put(8, "2025-08-30 00:00:00");
                put(9, "2025-09-29 00:00:00");
                put(10, "2025-10-30 00:00:00");
                put(11, "2025-11-29 00:00:00");
                put(12, "2025-12-30 00:00:00");
            }
        };

        CronExpression cronExpression = new CronExpression("0 0 0 L-1 * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }


    /**
     * 月末最后一个工作日
     */
    @Test
    public void testMonthEndWithWeekday() throws ParseException {
        Map<Integer, String> expectedResults = Maps.of(
                1, "2025-06-30 09:00:00",
                2, "2025-07-31 09:00:00",
                3, "2025-08-29 09:00:00",
                4, "2025-09-30 09:00:00",
                5, "2025-10-31 09:00:00",
                6, "2025-11-28 09:00:00",
                7, "2025-12-31 09:00:00",
                8, "2026-01-30 09:00:00",
                9, "2026-02-27 09:00:00",
                10, "2026-03-31 09:00:00",
                11, "2026-04-30 09:00:00",
                12, "2026-05-29 09:00:00"
        );
        CronExpression cronExpression = new CronExpression("0 0 9 LW * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 28, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Calendar nextCal = Calendar.getInstance();
            nextCal.setTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), DateUtil.formatDateTime(nextTriggerTime));
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 闰年2月
     */
    @Test
    public void testFebruaryLeapYear() throws ParseException {
        CronExpression cronExpression = new CronExpression("0 0 0 L * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
        Assertions.assertEquals(DateUtil.parseDateTime("2024-02-29 00:00:00"), nextTriggerTime);
    }

    @Test
    public void testEveryMinute() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:31:00",
                2, "2024-01-01 12:32:00",
                3, "2024-01-01 12:33:00",
                4, "2024-01-01 12:34:00",
                5, "2024-01-01 12:35:00"
        );

        CronExpression cronExpression = new CronExpression("0 * * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    @Test
    public void testEveryHour() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 13:00:00",
                2, "2024-01-01 14:00:00",
                3, "2024-01-01 15:00:00",
                4, "2024-01-01 16:00:00",
                5, "2024-01-01 17:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 每天指定时间
     */
    @Test
    public void testEveryDayAtSpecificTime() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-02 08:30:00",
                2, "2024-01-03 08:30:00",
                3, "2024-01-04 08:30:00",
                4, "2024-01-05 08:30:00",
                5, "2024-01-06 08:30:00"
        );

        CronExpression cronExpression = new CronExpression("0 30 8 * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    @Test
    public void testEveryWeekday() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>();
        expectedResults.put(1, "2024-01-02 09:00:00");
        expectedResults.put(2, "2024-01-03 09:00:00");
        expectedResults.put(3, "2024-01-04 09:00:00");
        expectedResults.put(4, "2024-01-05 09:00:00");
        expectedResults.put(5, "2024-01-08 09:00:00");
        expectedResults.put(6, "2024-01-09 09:00:00");
        expectedResults.put(7, "2024-01-10 09:00:00");
        expectedResults.put(8, "2024-01-11 09:00:00");
        expectedResults.put(9, "2024-01-12 09:00:00");
        expectedResults.put(10, "2024-01-15 09:00:00");

        CronExpression cronExpression = new CronExpression("0 0 9 ? * MON-FRI");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 10; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    @Test
    public void testEveryWeekend() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-06 10:00:00",
                2, "2024-01-07 10:00:00",
                3, "2024-01-13 10:00:00",
                4, "2024-01-14 10:00:00",
                5, "2024-01-20 10:00:00",
                6, "2024-01-21 10:00:00",
                7, "2024-01-27 10:00:00",
                8, "2024-01-28 10:00:00",
                9, "2024-02-03 10:00:00",
                10, "2024-02-04 10:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 10 ? * SAT,SUN");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 10; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 指定每月的第几天
     */
    @Test
    public void testSpecificDayOfMonth() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {{
            put(1, "2025-01-15 12:00:00");
            put(2, "2025-02-15 12:00:00");
            put(3, "2025-03-15 12:00:00");
            put(4, "2025-04-15 12:00:00");
            put(5, "2025-05-15 12:00:00");
            put(6, "2025-06-15 12:00:00");
            put(7, "2025-07-15 12:00:00");
            put(8, "2025-08-15 12:00:00");
            put(9, "2025-09-15 12:00:00");
            put(10, "2025-10-15 12:00:00");
            put(11, "2025-11-15 12:00:00");
            put(12, "2025-12-15 12:00:00");
        }};

        CronExpression cronExpression = new CronExpression("0 0 12 15 * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 每月的最后一个星期五
     */
    @Test
    public void testLastDayOfWeekInMonth() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2025-01-31 18:00:00");
                put(2, "2025-02-28 18:00:00");
                put(3, "2025-03-28 18:00:00");
                put(4, "2025-04-25 18:00:00");
                put(5, "2025-05-30 18:00:00");
                put(6, "2025-06-27 18:00:00");
                put(7, "2025-07-25 18:00:00");
                put(8, "2025-08-29 18:00:00");
                put(9, "2025-09-26 18:00:00");
                put(10, "2025-10-31 18:00:00");
                put(11, "2025-11-28 18:00:00");
                put(12, "2025-12-26 18:00:00");
            }
        };
        CronExpression cronExpression = new CronExpression("0 0 18 ? * 6L");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 每月第二周的星期一
     */
    @Test
    public void testNthDayOfWeekInMonth() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2024-01-08 09:00:00");
                put(2, "2024-02-12 09:00:00");
                put(3, "2024-03-11 09:00:00");
                put(4, "2024-04-08 09:00:00");
                put(5, "2024-05-13 09:00:00");
                put(6, "2024-06-10 09:00:00");
                put(7, "2024-07-08 09:00:00");
                put(8, "2024-08-12 09:00:00");
                put(9, "2024-09-09 09:00:00");
                put(10, "2024-10-14 09:00:00");
                put(11, "2024-11-11 09:00:00");
                put(12, "2024-12-09 09:00:00");
            }
        };

        CronExpression cronExpression = new CronExpression("0 0 9 ? * 2#2");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    @Test
    public void testIntervalExecution() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:45:00",
                2, "2024-01-01 13:00:00",
                3, "2024-01-01 13:15:00",
                4, "2024-01-01 13:30:00",
                5, "2024-01-01 13:45:00",
                6, "2024-01-01 14:00:00",
                7, "2024-01-01 14:15:00",
                8, "2024-01-01 14:30:00"
        );

        CronExpression cronExpression = new CronExpression("0 */15 * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 8; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 小时范围
     */
    @Test
    public void testRangeExecution() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 13:00:00",
                2, "2024-01-01 14:00:00",
                3, "2024-01-01 15:00:00",
                4, "2024-01-01 16:00:00",
                5, "2024-01-01 17:00:00",
                6, "2024-01-02 09:00:00",
                7, "2024-01-02 10:00:00",
                8, "2024-01-02 11:00:00",
                9, "2024-01-02 12:00:00",
                10, "2024-01-02 13:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 9-17 * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 10; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 指定星期几
     */
    @Test
    public void testComplexExpression() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {
            {
                put(1, "2025-06-30 09:30:00");
                put(2, "2025-07-02 09:30:00");
                put(3, "2025-07-04 09:30:00");
                put(4, "2025-07-07 09:30:00");
                put(5, "2025-07-09 09:30:00");
                put(6, "2025-07-11 09:30:00");
                put(7, "2025-07-14 09:30:00");
                put(8, "2025-07-16 09:30:00");
            }
        };

        CronExpression cronExpression = new CronExpression("0 30 9 ? * MON,WED,FRI");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 28, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 8; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 每月31号
     */
    @Test
    public void test31stDayOfMonth() throws ParseException {
        Map<Integer, String> expectedResults1 = Map.of(
                1, "2024-01-31 23:00:00",
                2, "2024-03-31 23:00:00",
                3, "2024-05-31 23:00:00",
                4, "2024-07-31 23:00:00",
                5, "2024-08-31 23:00:00"
        );

        CronExpression cronExpression1 = new CronExpression("0 0 23 31 * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 31, 22, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression1.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults1.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }

        CronExpression cronExpression2 = new CronExpression("0 0 0 1 1 ?");
        cal.set(2024, Calendar.DECEMBER, 31, 23, 30, 0);
        lastTriggerTime = cal.getTime();
        Date nextTriggerTime = cronExpression2.getNextValidTimeAfter(lastTriggerTime);
        Assertions.assertEquals(DateUtil.parseDateTime("2025-01-01 00:00:00"), nextTriggerTime);
    }

    @Test
    public void testNextMinute() throws ParseException {
        CronExpression cronExpression = new CronExpression("0 30 8 * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 8, 29, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
        Assertions.assertEquals(DateUtil.parseDateTime("2024-01-01 08:30:00"), nextTriggerTime);
    }

    @Test
    public void testWeekdayHandling() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2025-06-30 09:00:00",
                2, "2025-07-01 09:00:00",
                3, "2025-07-02 09:00:00",
                4, "2025-07-03 09:00:00",
                5, "2025-07-04 09:00:00",
                6, "2025-07-07 09:00:00",
                7, "2025-07-08 09:00:00",
                8, "2025-07-09 09:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 9 ? * MON-FRI");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JUNE, 28, 8, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 8; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 五分钟执行一次
     */
    @Test
    public void testConsecutiveExecution() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:05:00",
                2, "2024-01-01 12:10:00",
                3, "2024-01-01 12:15:00",
                4, "2024-01-01 12:20:00",
                5, "2024-01-01 12:25:00",
                6, "2024-01-01 12:30:00",
                7, "2024-01-01 12:35:00",
                8, "2024-01-01 12:40:00",
                9, "2024-01-01 12:45:00",
                10, "2024-01-01 12:50:00"
        );

        CronExpression cronExpression = new CronExpression("0 */5 * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 10; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 指定日期的边界情况
     */
    @Test
    public void testYearBoundary() throws ParseException {
        CronExpression cronExpression = new CronExpression("0 0 0 1 1 ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        Date lastTriggerTime = cal.getTime();
        Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTime(nextTriggerTime);

        Assertions.assertEquals("2025-01-01 00:00:00", DateUtil.formatDateTime(nextTriggerTime));
    }

    /**
     * 指定秒
     */
    @Test
    public void testSecondsField() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:30:30",
                2, "2024-01-01 12:31:30",
                3, "2024-01-01 12:32:30",
                4, "2024-01-01 12:33:30",
                5, "2024-01-01 12:34:30"
        );

        CronExpression cronExpression = new CronExpression("30 * * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 秒间隔
     */
    @Test
    public void testSecondsInterval() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:30:15",
                2, "2024-01-01 12:30:30",
                3, "2024-01-01 12:30:45",
                4, "2024-01-01 12:31:00",
                5, "2024-01-01 12:31:15"
        );

        CronExpression cronExpression = new CronExpression("*/15 * * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 特定月份
     */
    @Test
    public void testMonthSpecific() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-03-01 00:00:00",
                2, "2025-03-01 00:00:00",
                3, "2026-03-01 00:00:00",
                4, "2027-03-01 00:00:00",
                5, "2028-03-01 00:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 0 1 3 ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 月份范围
     */
    @Test
    public void testMonthRange() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-03-01 00:00:00",
                2, "2024-04-01 00:00:00",
                3, "2024-05-01 00:00:00",
                4, "2024-06-01 00:00:00",
                5, "2025-03-01 00:00:00",
                6, "2025-04-01 00:00:00",
                7, "2025-05-01 00:00:00",
                8, "2025-06-01 00:00:00"
        );

        CronExpression cronExpression = new CronExpression("0 0 0 1 3-6 ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 8; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Assertions.assertEquals(DateUtil.parseDateTime(expectedResults.get(i)), nextTriggerTime);
            lastTriggerTime = nextTriggerTime;
        }
    }


    /**
     * 无效表达式
     */
    @Test
    public void testInvalidExpressions() {
        // 测试无效表达式异常
        String[] invalidExpressions = {
                "",                      // 空表达式
                "abcdef",               // 无效表达式
                "0 0 0 * *",            // 缺少字段
                "0 0 0 * * * *",        // 字段过多
                "0 60 0 * * ?",         // 分钟超出范围
                "0 0 25 * * ?",         // 小时超出范围
                "0 0 0 32 * ?",         // 日期超出范围
                "0 0 0 * 13 ?",         // 月份超出范围
                "0 0 0 * * 8",          // 星期超出范围
                "0 0 0 L * 1",          // L和星期冲突
                "0 0 0 1 * 1",          // 日期和星期冲突
                "0 0 0 ? * 1#6",        // #值超出范围
                "0 0 0 L-32 * ?",       // L偏移超出范围
        };

        for (String expression : invalidExpressions) {
            try {
                new CronExpression(expression);
                Assertions.fail("应该抛出异常但通过了: " + expression);
            } catch (ParseException e) {
                Assertions.assertNotNull(e.getMessage(), "异常消息不应该为null");
            }
        }
    }

    /**
     * 秒范围
     */
    @Test
    public void testSecondsRange() throws ParseException {
        Map<Integer, String> expectedResults = Map.of(
                1, "2024-01-01 12:30:01",
                2, "2024-01-01 12:30:02",
                3, "2024-01-01 12:30:03",
                4, "2024-01-01 12:31:00",
                5, "2024-01-01 12:31:01",
                6, "2024-01-01 12:31:02",
                7, "2024-01-01 12:31:03",
                8, "2024-01-01 12:32:00",
                9, "2024-01-01 12:32:01",
                10, "2024-01-01 12:32:02"
        );
        CronExpression cronExpression = new CronExpression("0-3 * * * * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 12, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 10; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Calendar nextCal = Calendar.getInstance();
            nextCal.setTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), DateUtil.formatDateTime(nextTriggerTime));
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 跨月边界
     */
    @Test
    public void testCrossMonthBoundary() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {{
            put(1, "2025-03-31 00:00:00");
            put(2, "2025-05-31 00:00:00");
            put(3, "2025-07-31 00:00:00");
            put(4, "2025-08-31 00:00:00");
            put(5, "2025-10-31 00:00:00");
            put(6, "2025-12-31 00:00:00");
            put(7, "2026-01-31 00:00:00");
            put(8, "2026-03-31 00:00:00");
            put(9, "2026-05-31 00:00:00");
            put(10, "2026-07-31 00:00:00");
            put(11, "2026-08-31 00:00:00");
            put(12, "2026-10-31 00:00:00");
        }};
        CronExpression cronExpression = new CronExpression("0 0 0 31 * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            Calendar nextCal = Calendar.getInstance();
            nextCal.setTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), DateUtil.formatDateTime(nextTriggerTime));
            lastTriggerTime = nextTriggerTime;
        }
    }

    @Test
    public void testNearestWeekday() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {{
            put(1, "2025-07-10 00:00:00");
            put(2, "2025-08-11 00:00:00");
            put(3, "2025-09-10 00:00:00");
            put(4, "2025-10-10 00:00:00");
            put(5, "2025-11-10 00:00:00");
            put(6, "2025-12-10 00:00:00");
            put(7, "2026-01-09 00:00:00");
            put(8, "2026-02-10 00:00:00");
            put(9, "2026-03-10 00:00:00");
            put(10, "2026-04-10 00:00:00");
            put(11, "2026-05-11 00:00:00");
            put(12, "2026-06-10 00:00:00");
        }};
        CronExpression cronExpression = new CronExpression("0 0 0 10W * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 5, 0, 0, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            String actualTime = DateUtil.formatDateTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), actualTime);
            lastTriggerTime = nextTriggerTime;
        }
    }


    /**
     * 最近工作日指定日期为月初边界情况，比如1号是周六，应该得到3号
     */
    @Test
    public void testNearestWeekdayOfMonthStart() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {{
            put(1, "2025-08-01 00:00:00");
            put(2, "2025-09-01 00:00:00");
            put(3, "2025-10-01 00:00:00");
            put(4, "2025-11-03 00:00:00");
            put(5, "2025-12-01 00:00:00");
            put(6, "2026-01-01 00:00:00");
            put(7, "2026-02-02 00:00:00");
            put(8, "2026-03-02 00:00:00");
            put(9, "2026-04-01 00:00:00");
            put(10, "2026-05-01 00:00:00");
            put(11, "2026-06-01 00:00:00");
            put(12, "2026-07-01 00:00:00");
        }};
        CronExpression cronExpression = new CronExpression("0 0 0 1W * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 5, 0, 0, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            String actualTime = DateUtil.formatDateTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), actualTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

    /**
     * 最近工作日指定日期为月末边界情况
     */
    @Test
    public void testNearestWeekdayOfMonthEnd() throws ParseException {
        Map<Integer, String> expectedResults = new HashMap<>() {{
            put(1, "2025-07-30 00:00:00");
            put(2, "2025-08-29 00:00:00");
            put(3, "2025-09-30 00:00:00");
            put(4, "2025-10-30 00:00:00");
            put(5, "2025-11-28 00:00:00");
            put(6, "2025-12-30 00:00:00");
            put(7, "2026-01-30 00:00:00");
            put(8, "2026-03-30 00:00:00");
            put(9, "2026-04-30 00:00:00");
            put(10, "2026-05-29 00:00:00");
            put(11, "2026-06-30 00:00:00");
            put(12, "2026-07-30 00:00:00");
        }};
        CronExpression cronExpression = new CronExpression("0 0 0 30W * ?");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 5, 0, 0, 0);

        Date lastTriggerTime = cal.getTime();
        for (int i = 1; i <= 12; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            String actualTime = DateUtil.formatDateTime(nextTriggerTime);
            Assertions.assertEquals(expectedResults.get(i), actualTime);
            lastTriggerTime = nextTriggerTime;
        }
    }

}
