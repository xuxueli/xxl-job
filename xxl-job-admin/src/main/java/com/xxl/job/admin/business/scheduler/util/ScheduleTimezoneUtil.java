package com.xxl.job.admin.business.scheduler.util;

import com.xxl.tool.core.StringTool;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

public class ScheduleTimezoneUtil {

    private ScheduleTimezoneUtil() {
    }

    public static List<String> getAvailableTimezoneIds() {
        return ZoneId.getAvailableZoneIds().stream().sorted().toList();
    }

    public static String getDefaultTimezoneId() {
        return ZoneId.systemDefault().getId();
    }

    public static boolean isValidTimezone(String timezoneId) {
        if (StringTool.isBlank(timezoneId)) {
            return true;
        }
        return ZoneId.getAvailableZoneIds().contains(timezoneId);
    }

    public static TimeZone resolveTimezone(String timezoneId) {
        if (StringTool.isBlank(timezoneId)) {
            return TimeZone.getDefault();
        }
        return TimeZone.getTimeZone(ZoneId.of(timezoneId));
    }
}
