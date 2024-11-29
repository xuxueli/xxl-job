package com.xxl.job.admin.core.complete;

import java.text.MessageFormat;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobContext;
import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2020-10-30 20:43:10
 */
public class XxlJobCompleter {

	/**
	 * common fresh handle entrance (limit only once)
	 */
	public static int updateHandleInfoAndFinish(XxlJobLog xxlJobLog) {

		// finish
		finishJob(xxlJobLog);

		// text最大64kb 避免长度过长
		if (xxlJobLog.getHandleMsg().length() > 15000) {
			xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg().substring(0, 15000));
		}

		// fresh handle
		return XxlJobAdminConfig.getAdminConfig().getXxlJobLogDao().updateHandleInfo(xxlJobLog);
	}

	/**
	 * do somethind to finish job
	 */
	private static void finishJob(XxlJobLog xxlJobLog) {

		// 1、handle success, to trigger child job
		StringBuilder triggerChildMsg = null;
		if (XxlJobContext.HANDLE_CODE_SUCCESS == xxlJobLog.getHandleCode()) {
			XxlJobInfo xxlJobInfo = XxlJobAdminConfig.getAdminConfig().getXxlJobInfoDao().loadById(xxlJobLog.getJobId());
			if (xxlJobInfo != null && StringUtils.hasText(xxlJobInfo.getChildJobId())) {
				triggerChildMsg = new StringBuilder("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>")
						.append(I18nUtil.getString("jobconf_trigger_child_run"))
						.append("<<<<<<<<<<< </span><br>");

				String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
				for (int i = 0; i < childJobIds.length; i++) {
					int childJobId = getInt(childJobIds[i], -1);
					if (childJobId > 0) {
						JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
						ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

						// add msg
						triggerChildMsg.append(MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
								(i + 1),
								childJobIds.length,
								childJobIds[i],
								(triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? I18nUtil.getString("system_success") : I18nUtil.getString("system_fail")),
								triggerChildResult.getMsg()));
					} else {
						triggerChildMsg.append(MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
								(i + 1),
								childJobIds.length,
								childJobIds[i]));
					}
				}

			}
		}

		if (triggerChildMsg != null) {
			xxlJobLog.setHandleMsg(xxlJobLog.getHandleMsg() + triggerChildMsg);
		}

		// 2、fix_delay trigger next
		// on the way

	}

	public static int getInt(String str, int defaultVal) {
		if (StringUtils.hasText(str)) {
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException ignored) {
			}
		}
		return defaultVal;
	}

}
