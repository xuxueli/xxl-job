package com.xxl.job.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xxl.job.admin.core.alarm.JobAlarmerEnum;
import com.xxl.job.admin.core.model.XxlAlarmInfo;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlAlarmInfoDao;
import com.xxl.job.admin.service.AlarmService;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * 
 * @author Locki 2019-11-14 20:53:22
 *
 */
@Service
public class AlarmServiceImpl implements AlarmService {
	@Resource
	private XxlAlarmInfoDao xxlAlarmInfoDao;

	@Override
	public Map<String, Object> pageList(int start, int length, String alarmEnum, String alarmName, String alarmDesc) {
		List<XxlAlarmInfo> list = xxlAlarmInfoDao.pageList(start, length, alarmEnum, alarmName, alarmDesc);
		int list_count = xxlAlarmInfoDao.pageListCount(start, length, alarmEnum, alarmName, alarmDesc);
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", list_count); // 总记录数
		maps.put("recordsFiltered", list_count); // 过滤后的总记录数
		maps.put("data", list); // 分页列表
		return maps;
	}

	@Override
	public ReturnT<String> add(XxlAlarmInfo alarmInfo) {
		// valid
		if (JobAlarmerEnum.match(alarmInfo.getAlarmType(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("alarminfo_type") + I18nUtil.getString("system_unvalid")));
		}
		if (alarmInfo.getAlarmName() == null || alarmInfo.getAlarmName().trim().length() == 0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("alarminfo_name")));
		}
		
		xxlAlarmInfoDao.save(alarmInfo);
		if (alarmInfo.getId() < 1) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("alarminfo_field_add")+I18nUtil.getString("system_fail")) );
		}

		return new ReturnT<String>(String.valueOf(alarmInfo.getId()));
	}

	@Override
	public ReturnT<String> update(XxlAlarmInfo alarmInfo) {
		// valid
		if (JobAlarmerEnum.match(alarmInfo.getAlarmType(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("alarminfo_type") + I18nUtil.getString("system_unvalid")));
		}
		if (alarmInfo.getAlarmName() == null || alarmInfo.getAlarmName().trim().length() == 0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input") + I18nUtil.getString("alarminfo_name")));
		}
		
		// stage job info
		XxlAlarmInfo exists_alarmInfo = xxlAlarmInfoDao.loadById(alarmInfo.getId());
		if (exists_alarmInfo == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("alarminfo_alarm") + "ID" + I18nUtil.getString("system_not_found")) );
		}
		
		xxlAlarmInfoDao.update(alarmInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> remove(long id) {
		xxlAlarmInfoDao.delete(id);
		return ReturnT.SUCCESS;
	}
}
