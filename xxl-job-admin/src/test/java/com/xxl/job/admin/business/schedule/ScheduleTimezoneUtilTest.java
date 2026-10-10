package com.xxl.job.admin.business.schedule;

import com.xxl.job.admin.business.scheduler.util.ScheduleTimezoneUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleTimezoneUtilTest {

    @Test
    void acceptsSupportedIanaTimezoneAndLegacyBlankValue() {
        assertTrue(ScheduleTimezoneUtil.isValidTimezone("Asia/Shanghai"));
        assertTrue(ScheduleTimezoneUtil.isValidTimezone(null));
    }

    @Test
    void rejectsUnknownTimezoneAndRawOffset() {
        assertFalse(ScheduleTimezoneUtil.isValidTimezone("Mars/Olympus_Mons"));
        assertFalse(ScheduleTimezoneUtil.isValidTimezone("+08:00"));
    }
}
