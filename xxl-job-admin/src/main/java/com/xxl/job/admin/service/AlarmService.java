package com.xxl.job.admin.service;

import java.util.Map;

import com.xxl.job.admin.core.model.XxlAlarmInfo;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * 
 * @author Locki 2019-11-14 20:51:56
 *
 */
public interface AlarmService {

	public Map<String, Object> pageList(int start, int length, String alarmEnum, String alarmName, String alarmDesc);

	public ReturnT<String> add(XxlAlarmInfo alarmInfo);

	public ReturnT<String> update(XxlAlarmInfo alarmInfo);

	public ReturnT<String> remove(long id);
}
