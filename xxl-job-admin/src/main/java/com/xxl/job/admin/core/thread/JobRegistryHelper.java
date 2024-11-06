package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * job registry instance
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryHelper {
	private static Logger logger = LoggerFactory.getLogger(JobRegistryHelper.class);

	private static JobRegistryHelper instance = new JobRegistryHelper();
	public static JobRegistryHelper getInstance(){
		return instance;
	}

	private ThreadPoolExecutor registryOrRemoveThreadPool = null;
	private Thread registryMonitorThread;
	private volatile boolean toStop = false;

	public void start(){

		// for registry or remove
		registryOrRemoveThreadPool = new ThreadPoolExecutor(
				2,
				10,
				30L,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(2000),
				r -> new Thread(r, "xxl-job, admin JobRegistryMonitorHelper-registryOrRemoveThreadPool-" + r.hashCode()),
				(r, executor) -> {
					r.run();
					logger.warn(">>>>>>>>>>> xxl-job, registry or remove too fast, match threadpool rejected handler(run now).");
				});

		// for monitor
		registryMonitorThread = new Thread(() -> {
			while (!toStop) {
				try {
					// auto registry group
					final XxlJobAdminConfig adminConfig = XxlJobAdminConfig.getAdminConfig();
					List<XxlJobGroup> groupList = adminConfig.getXxlJobGroupDao().findByAddressType(0);
					if (groupList != null && !groupList.isEmpty()) {
						// remove dead address (admin/executor)
						List<Integer> ids = adminConfig.getXxlJobRegistryDao().findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
						if (ids != null && !ids.isEmpty()) {
							adminConfig.getXxlJobRegistryDao().removeDead(ids);
						}

						// fresh online address (admin/executor)
						List<XxlJobRegistry> list = adminConfig.getXxlJobRegistryDao().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
						final Map<String, TreeSet<String>> appAddressMap = groupJobsByApp(list);

						// fresh group address
						final Date now = new Date();
						for (XxlJobGroup group : groupList) {
							TreeSet<String> registryList = appAddressMap.get(group.getAppname());
							String addressListStr = null;
							if (registryList != null && !registryList.isEmpty()) {
								addressListStr = String.join(",", registryList);
							}
							group.setAddressList(addressListStr);
							group.setUpdateTime(now);
							adminConfig.getXxlJobGroupDao().update(group);
						}
					}
				} catch (Exception e) {
					if (!toStop) {
						logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error", e);
					}
				}
				try {
					TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
				} catch (InterruptedException e) {
					if (!toStop) {
						logger.error(">>>>>>>>>>> xxl-job, job registry monitor thread error", e);
					}
				}
			}
			logger.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
		});
		registryMonitorThread.setDaemon(true);
		registryMonitorThread.setName("xxl-job, admin JobRegistryMonitorHelper-registryMonitorThread");
		registryMonitorThread.start();
	}

	public static Map<String, TreeSet<String>> groupJobsByApp(List<XxlJobRegistry> list) {
		if (list != null && !list.isEmpty()) {
			final Map<String, TreeSet<String>> appJobMap = new HashMap<>();
			for (XxlJobRegistry item : list) {
				if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
					String appname = item.getRegistryKey();
					TreeSet<String> registries = appJobMap.get(appname);
					if (registries == null) {
						appJobMap.put(appname, registries = new TreeSet<>());
					}
					registries.add(item.getRegistryValue());
				}
			}
			return appJobMap;
		}
		return Collections.emptyMap();
	}

	public void toStop() {
		toStop = true;

		// stop registryOrRemoveThreadPool
		registryOrRemoveThreadPool.shutdownNow();

		// stop monitir (interrupt and wait)
		registryMonitorThread.interrupt();
		try {
			registryMonitorThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}


	// ---------------------- helper ----------------------

	public ReturnT<String> registry(RegistryParam registryParam) {

		// valid
		if (!StringUtils.hasText(registryParam.getRegistryGroup())
				|| !StringUtils.hasText(registryParam.getRegistryKey())
				|| !StringUtils.hasText(registryParam.getRegistryValue())) {
			return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument.");
		}

		// async execute
		registryOrRemoveThreadPool.execute(() -> {
			int ret = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
			if (ret < 1) {
				XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());

				// fresh
				freshGroupRegistryInfo(registryParam);
			}
		});

		return ReturnT.SUCCESS;
	}

	public ReturnT<String> registryRemove(RegistryParam registryParam) {

		// valid
		if (!StringUtils.hasText(registryParam.getRegistryGroup())
				|| !StringUtils.hasText(registryParam.getRegistryKey())
				|| !StringUtils.hasText(registryParam.getRegistryValue())) {
			return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument.");
		}

		// async execute
		registryOrRemoveThreadPool.execute(() -> {
			int ret = XxlJobAdminConfig.getAdminConfig().getXxlJobRegistryDao().registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
			if (ret > 0) {
				// fresh
				freshGroupRegistryInfo(registryParam);
			}
		});

		return ReturnT.SUCCESS;
	}

	private void freshGroupRegistryInfo(RegistryParam registryParam){
		// Under consideration, prevent affecting core tables
	}


}
