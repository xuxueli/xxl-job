package com.xxl.job.admin.business.scheduler.alarm;

import com.xxl.job.admin.business.model.XxlJobLog;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * alarm streak policy test
 */
public class AlarmStreakPolicyTest {

    private XxlJobLog log(int triggerCode, int handleCode) {
        XxlJobLog log = new XxlJobLog();
        log.setTriggerCode(triggerCode);
        log.setHandleCode(handleCode);
        return log;
    }

    @Test
    public void testBelowThreshold() {
        List<XxlJobLog> two = new ArrayList<>();
        two.add(log(200, 500));
        two.add(log(500, 0));
        assertFalse(AlarmStreakPolicy.isStreakFail(two, 3));
    }

    @Test
    public void testStreakReachThreshold() {
        List<XxlJobLog> three = new ArrayList<>();
        three.add(log(200, 500));   // latest first
        three.add(log(500, 0));
        three.add(log(200, 500));
        assertTrue(AlarmStreakPolicy.isStreakFail(three, 3));
    }

    @Test
    public void testStreakBrokenBySuccess() {
        List<XxlJobLog> logs = new ArrayList<>();
        logs.add(log(200, 500));
        logs.add(log(200, 200));   // success breaks the streak
        logs.add(log(200, 500));
        assertFalse(AlarmStreakPolicy.isStreakFail(logs, 3));
    }

    @Test
    public void testMisfireBreaksStreak() {
        List<XxlJobLog> logs = new ArrayList<>();
        logs.add(log(200, 500));
        logs.add(log(0, 0));       // misfire is not a failure
        logs.add(log(200, 500));
        assertFalse(AlarmStreakPolicy.isStreakFail(logs, 3));
    }

    @Test
    public void testInFlightBreaksStreak() {
        List<XxlJobLog> logs = new ArrayList<>();
        logs.add(log(200, 500));
        logs.add(log(200, 0));     // in-flight is not a failure yet
        logs.add(log(200, 500));
        assertFalse(AlarmStreakPolicy.isStreakFail(logs, 3));
    }

    @Test
    public void testNullAndInvalidInput() {
        assertFalse(AlarmStreakPolicy.isStreakFail(null, 3));
        assertFalse(AlarmStreakPolicy.isStreakFail(new ArrayList<XxlJobLog>(), 3));
        List<XxlJobLog> three = new ArrayList<>();
        three.add(log(200, 500));
        three.add(log(200, 500));
        three.add(log(200, 500));
        assertFalse(AlarmStreakPolicy.isStreakFail(three, 0));
    }

}
