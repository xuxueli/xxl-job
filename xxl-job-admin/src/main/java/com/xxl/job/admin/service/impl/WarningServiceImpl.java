package com.xxl.job.admin.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xxl.job.admin.service.WarningService;
/**
 * 
 * @author Locki 2019-11-14 20:53:22
 *
 */
@Service
public class WarningServiceImpl implements WarningService {

	@Override
	public Map<String, Object> pageList(int start, int length, String warningName, String warningParam,
			String warningDesc) {
		return new HashMap<String, Object>();
	}
}
