package com.xxl.job.admin.core.jobbean.impl;
//package com.xxl.job.action.job.impl;
//
//import java.util.concurrent.TimeUnit;
//
//import org.quartz.DisallowConcurrentExecution;
//
//import com.xxl.job.action.job.LocalNomalJobBean;
//
///**
// * demo job bean for no-concurrent
// * @author xuxueli 2016-3-12 14:25:14
// */
//@Deprecated
//@DisallowConcurrentExecution	// 串行；线程数要多配置几个，否则不生效；
//public class DemoConcurrentJobBean extends LocalNomalJobBean {
//
//	@Override
//	public Object handle(String... param) {
//		
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		return false;
//	}
//
//}
