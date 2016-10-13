package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.callback.XxlJobLogCallbackServer;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.util.DynamicSchedulerUtil;
import com.xxl.job.core.registry.RegistHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * job registry helper
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobRegistryHelper {
	private static Logger logger = LoggerFactory.getLogger(JobRegistryHelper.class);

	private static JobRegistryHelper helper = new JobRegistryHelper();
	private ConcurrentHashMap<String, List<String>> registMap = new ConcurrentHashMap<String, List<String>>();

	public JobRegistryHelper(){
		Thread registryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
                        // registry admin
                        int ret = DynamicSchedulerUtil.xxlJobRegistryDao.registryUpdate(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name(), XxlJobLogCallbackServer.getTrigger_log_address());
                        if (ret < 1) {
                            DynamicSchedulerUtil.xxlJobRegistryDao.registrySave(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name(), XxlJobLogCallbackServer.getTrigger_log_address());
                        }

                        // fresh registry map
						ConcurrentHashMap<String, List<String>> temp = new ConcurrentHashMap<String, List<String>>();
						DynamicSchedulerUtil.xxlJobRegistryDao.removeDead(RegistHelper.TIMEOUT*2);
						List<XxlJobRegistry> list = DynamicSchedulerUtil.xxlJobRegistryDao.findAll(RegistHelper.TIMEOUT*2);
						if (list != null) {
							for (XxlJobRegistry item: list) {
								String groupKey = makeGroupKey(item.getRegistryGroup(), item.getRegistryKey());
								List<String> registryList = temp.get(groupKey);
								if (registryList == null) {
									registryList = new ArrayList<String>();
								}
								registryList.add(item.getRegistryValue());
								temp.put(groupKey, registryList);
							}
						}
						registMap = temp;
					} catch (Exception e) {
						logger.error("job registry helper error:{}", e);
					}
					try {
						TimeUnit.SECONDS.sleep(RegistHelper.TIMEOUT);
					} catch (InterruptedException e) {
						logger.error("job registry helper error:{}", e);
					}
				}
			}
		});
		registryThread.setDaemon(true);
		registryThread.start();

	}

	private static String makeGroupKey(String registryGroup, String registryKey){
		return registryGroup.concat("_").concat(registryKey);
	}
	
	public static List<String> discover(String registryGroup, String registryKey){
		String groupKey = makeGroupKey(registryGroup, registryKey);
		return helper.registMap.get(groupKey);
	}
	
}
