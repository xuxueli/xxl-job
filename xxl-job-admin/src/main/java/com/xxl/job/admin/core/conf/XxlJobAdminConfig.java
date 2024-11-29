package com.xxl.job.admin.core.conf;

import java.util.Arrays;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.xxl.job.admin.core.alarm.JobAlarmer;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class XxlJobAdminConfig implements InitializingBean, DisposableBean {

	private static XxlJobAdminConfig adminConfig;
	private XxlJobScheduler xxlJobScheduler;

	public static XxlJobAdminConfig getAdminConfig() {
		return adminConfig;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		adminConfig = this;

		xxlJobScheduler = new XxlJobScheduler();
		xxlJobScheduler.init();
	}

	@Override
	public void destroy() throws Exception {
		xxlJobScheduler.destroy();
	}

	// conf
	@Value("${xxl.job.i18n}")
	private String i18n;

	@Value("${xxl.job.accessToken}")
	private String accessToken;

	@Value("${spring.mail.from}")
	private String emailFrom;

	@Value("${xxl.job.triggerpool.fast.max}")
	private int triggerPoolFastMax;

	@Value("${xxl.job.triggerpool.slow.max}")
	private int triggerPoolSlowMax;

	@Value("${xxl.job.logretentiondays}")
	private int logretentiondays;

	// dao, service

	@Resource
	private XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	private XxlJobRegistryDao xxlJobRegistryDao;
	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobLogReportDao xxlJobLogReportDao;
	@Resource
	private JavaMailSender mailSender;
	@Resource
	private DataSource dataSource;
	@Resource
	private JobAlarmer jobAlarmer;

	public String getI18n() {
		if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
			return "zh_CN";
		}
		return i18n;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public int getTriggerPoolFastMax() {
		return Math.max(triggerPoolFastMax, 200);
	}

	public int getTriggerPoolSlowMax() {
		return Math.max(triggerPoolSlowMax, 100);
	}

	public int getLogretentiondays() {
		if (logretentiondays < 7) {
			return -1;  // Limit greater than or equal to 7, otherwise close
		}
		return logretentiondays;
	}

	public XxlJobLogDao getXxlJobLogDao() {
		return xxlJobLogDao;
	}

	public XxlJobInfoDao getXxlJobInfoDao() {
		return xxlJobInfoDao;
	}

	public XxlJobRegistryDao getXxlJobRegistryDao() {
		return xxlJobRegistryDao;
	}

	public XxlJobGroupDao getXxlJobGroupDao() {
		return xxlJobGroupDao;
	}

	public XxlJobLogReportDao getXxlJobLogReportDao() {
		return xxlJobLogReportDao;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public JobAlarmer getJobAlarmer() {
		return jobAlarmer;
	}

}