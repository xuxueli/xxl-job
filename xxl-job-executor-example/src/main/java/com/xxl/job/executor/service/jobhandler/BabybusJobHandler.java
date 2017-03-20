package com.xxl.job.executor.service.jobhandler;

import org.springframework.stereotype.Service;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;

@JobHander(value="3bJobHandler")
@Service
public class BabybusJobHandler extends IJobHandler{

	@Override
	public void execute(String... params) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
