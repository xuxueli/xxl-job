package com.xxl.job.admin.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class XxlJobInfoTest {

    @Test
    public void testScheduleTimeZoneGetterSetter() {
        XxlJobInfo info = new XxlJobInfo();

        assertNull(info.getScheduleTimeZone());

        info.setScheduleTimeZone("Asia/Shanghai");
        assertEquals("Asia/Shanghai", info.getScheduleTimeZone());

        info.setScheduleTimeZone("Europe/Moscow");
        assertEquals("Europe/Moscow", info.getScheduleTimeZone());

        info.setScheduleTimeZone(null);
        assertNull(info.getScheduleTimeZone());

        info.setScheduleTimeZone("");
        assertEquals("", info.getScheduleTimeZone());
    }
}
