var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	},
	/**
	 * 配置单例job执行
	 */
	/*xxlJobExecutor : {
		type : "com.xxl.job.core.executor.XxlJobExecutor",
		events : {
			create : "start",
			depose : "destroy"
		},
		fields : {
			ip : {
				java : "$conf.get('xxl.job.executor.ip')"
			},
			port : {
				java : "$conf.get('xxl.job.executor.port')"
			},
			appName : {
				java : "$conf.get('xxl.job.executor.appname')"
			},
			adminAddresses : {
				java : "$conf.get('xxl.job.admin.addresses')"
			},
			logPath : {
				java : "$conf.get('xxl.job.executor.logpath')"
			},
			accessToken : {
				java : "$conf.get('xxl.job.accessToken')"
			}
		}
	}*/
};