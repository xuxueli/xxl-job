package com.xxl.job.admin.business.scheduler.alarm;

import com.xxl.job.admin.business.model.XxlJobLog;

import java.util.List;

/**
 * alarm streak policy
 *
 * Decide whether a failure should trigger an alarm, based on the recent trigger/handle
 * result streak of the same job. It helps to reduce alarm noise caused by one-off
 * transient failures (such as executor restarting), which is especially useful for
 * jobs scheduled at a high frequency.
 *
 * Fail semantics is aligned with XxlJobLogMapper#findFailJobLogIds:
 * handle_code != 200 AND (handle_code != 0 OR trigger_code NOT IN (0, 200))
 *
 * @author xxl-job contributors 2026
 */
public class AlarmStreakPolicy {

	/**
	 * whether the most recent <code>failCount</code> logs are all failed
	 *
	 * @param recentLogsDesc recent logs of the same job, ordered by id desc (latest first)
	 * @param failCount alarm threshold, alarm only when consecutive failures reach it
	 * @return true only if the streak of failures reaches the threshold
	 */
	public static boolean isStreakFail(List<XxlJobLog> recentLogsDesc, int failCount) {
		if (failCount < 1 || recentLogsDesc == null || recentLogsDesc.size() < failCount) {
			return false;
		}
		for (XxlJobLog log : recentLogsDesc) {
			if (!isFail(log)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * same fail semantics as findFailJobLogIds:
	 * misfire (0,0) and in-flight (200,0) are not failures, and they break the streak
	 */
	private static boolean isFail(XxlJobLog log) {
		if (log.getHandleCode() == 200) {
			return false;
		}
		if (log.getHandleCode() != 0) {
			return true;
		}
		return log.getTriggerCode() != 0 && log.getTriggerCode() != 200;
	}

}
