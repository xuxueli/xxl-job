package com.xxl.job.executor.core.config;

import java.util.Properties;

import com.candao.irms.framework.util.FileUtil;

public class URLConfig {

	// 服务Host
	public static String SERVER_HOST = "";
	
	public static String SIGN_KEY = "";

	// 账号服务-执行清除门店账号临时登录会话任务(每3天执行一次)
	public static String CLEAN_SHOP_TEMP_LOGIN_TOKENS = "";

	// 门店服务-执行门店规则延时生效数据修改任务(每1分钟执行一次)
	public static String UPDATE_STORE_RULE_DATA = "";

	// 订单服务-检查需要打印订单任务(每1分钟执行一次)
	public static String CHECK_PRINT_ORDER = "";

	// 订单服务-检查第三方配送自动汇集任务(每1分钟执行一次)
	public static String CHECK_EXT_RIDER_AUTO_READY = "";

	// 订单服务-检查第三方配送自动呼叫任务(每1分钟执行一次)
	public static String CHECK_ADVANCE_ORDER_EXT_DELIVERY_ORDER = "";

	// 订单服务-检查系统异常重推任务(每3分钟执行一次)
	public static String CHECK_SYS_PUSH_ORDER_EXCEPTION = "";

	// 报表服务-完成订单相关报表生成任务(每天02:30分执行)
	public static String GENERATE_FINISH_ORDER_REPORT = "";

	// 报表服务-门店相关报表生成任务(每天02:30分执行)
	public static String GENERATE_STORE_REPORT = "";

	// 报表服务-骑手上下班报表生成任务(每天02:30分执行)
	public static String GENERATE_WORK_HORSEMAN_REPORT = "";

	static {
		initConfig();
	}

	public static void initConfig() {
		try {
			Properties properties = FileUtil.getConfigProperties("task_url.properties");
			SERVER_HOST = properties.getProperty("SERVER_HOST");
			SIGN_KEY = properties.getProperty("SIGN_KEY");

			CLEAN_SHOP_TEMP_LOGIN_TOKENS = SERVER_HOST + properties.getProperty("CLEAN_SHOP_TEMP_LOGIN_TOKENS");
			UPDATE_STORE_RULE_DATA = SERVER_HOST + properties.getProperty("UPDATE_STORE_RULE_DATA");
			CHECK_PRINT_ORDER = SERVER_HOST + properties.getProperty("CHECK_PRINT_ORDER");
			CHECK_EXT_RIDER_AUTO_READY = SERVER_HOST + properties.getProperty("CHECK_EXT_RIDER_AUTO_READY");
			CHECK_ADVANCE_ORDER_EXT_DELIVERY_ORDER = SERVER_HOST + properties.getProperty("CHECK_ADVANCE_ORDER_EXT_DELIVERY_ORDER");
			CHECK_SYS_PUSH_ORDER_EXCEPTION = SERVER_HOST + properties.getProperty("CHECK_SYS_PUSH_ORDER_EXCEPTION");
			GENERATE_FINISH_ORDER_REPORT = SERVER_HOST + properties.getProperty("GENERATE_FINISH_ORDER_REPORT");
			GENERATE_STORE_REPORT = SERVER_HOST + properties.getProperty("GENERATE_STORE_REPORT");
			GENERATE_WORK_HORSEMAN_REPORT = SERVER_HOST + properties.getProperty("GENERATE_WORK_HORSEMAN_REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
