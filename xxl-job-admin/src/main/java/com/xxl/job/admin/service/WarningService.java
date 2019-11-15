package com.xxl.job.admin.service;

import java.util.Map;
/**
 * 
 * @author Locki 2019-11-14 20:51:56
 *
 */
public interface WarningService {

	public Map<String, Object> pageList(int start, int length, String warningName, String warningParam, String warningDesc);

}
