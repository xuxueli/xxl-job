package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.core.registry.RegistHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

	private ConcurrentHashMap<String, List<String>> registMap = new ConcurrentHashMap<String, List<String>>();

	private Thread registryThread;
	private boolean toStop = false;
	public void start(){
		registryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!toStop) {
					try {
                        // registry admin
                        int ret = XxlJobDynamicScheduler.xxlJobRegistryDao.registryUpdate(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name(), XxlJobDynamicScheduler.getCallbackAddress());
                        if (ret < 1) {
                            XxlJobDynamicScheduler.xxlJobRegistryDao.registrySave(RegistHelper.RegistType.ADMIN.name(), RegistHelper.RegistType.ADMIN.name(), XxlJobDynamicScheduler.getCallbackAddress());
                        }

                        // remove dead admin/executor
						XxlJobDynamicScheduler.xxlJobRegistryDao.removeDead(RegistHelper.TIMEOUT*2);

                        // fresh registry map
						ConcurrentHashMap<String, List<String>> temp = new ConcurrentHashMap<String, List<String>>();
						List<XxlJobRegistry> list = XxlJobDynamicScheduler.xxlJobRegistryDao.findAll(RegistHelper.TIMEOUT*2);
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
						logger.error("job registry instance error:{}", e);
					}
					try {
						TimeUnit.SECONDS.sleep(RegistHelper.TIMEOUT);
					} catch (InterruptedException e) {
						logger.error("job registry instance error:{}", e);
					}
				}
			}
		});
		registryThread.setDaemon(true);
		registryThread.start();
	}

	public void toStop(){
		toStop = true;
		//registryThread.interrupt();
	}

	private static String makeGroupKey(String registryGroup, String registryKey){
		return registryGroup.concat("_").concat(registryKey);
	}
	
	public static List<String> discover(String registryGroup, String registryKey){
		String groupKey = makeGroupKey(registryGroup, registryKey);
		return instance.registMap.get(groupKey);
	}
	
}
