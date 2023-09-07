package com.xxl.job.admin.thread;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.pojo.dto.JobGroupDTO;
import com.xxl.job.admin.common.pojo.entity.Registry;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.RegistryService;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.enums.RegistryType;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务注册线程
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class RegistryThread extends AbstractThreadListener implements Ordered {

	@Autowired
	private JobGroupService jobGroupService;

	@Autowired
	private RegistryService registryService;

	private ThreadPoolExecutor registryOrRemoveThreadPool = null;
	private Thread registryMonitorThread;
	private volatile boolean toStop = false;

	@Override
	public int getOrder() {
		return 2;
	}

	@Override
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
					log.warn(">>>>>>>>>>> xxl-job, registry or remove too fast, match thread pool rejected handler(run now).");
				});

		// for monitor
		registryMonitorThread = new Thread(new Runnable() {
			@Override
			public void run() {

				log.info(">>>>>>>>>>> xxl-job, job registry monitor thread start...");

				while (!toStop) {
					try {
						// auto registry group
						List<JobGroupVO> jobGroups = jobGroupService.queryJobGroupByAddressType(NumberConstant.ZERO);
						if (CollectionUtil.isNotEmpty(jobGroups)) {

							// remove dead address (admin/executor)
							List<Long> ids = registryService.findDeadRegistryByUpdatedTime(DateUtil.offsetSecond(DateUtil.date(),
									RegistryConfig.DEAD_TIMEOUT.getValue() * NumberConstant.A_NEGATIVE).getTime());
							if (CollectionUtil.isNotEmpty(ids)) {
								registryService.deleteDeadRegistryByIds(ids);
							}

							// fresh online address (admin/executor)
							HashMap<String, List<String>> appAddressMap = MapUtil.newHashMap();

							List<Registry> registries = registryService.findAll(DateUtil.offsetSecond(DateUtil.date(),
									RegistryConfig.DEAD_TIMEOUT.getValue() * NumberConstant.A_NEGATIVE).getTime());
							if (CollectionUtil.isNotEmpty(registries)) {
								for (Registry registry: registries) {
									if (RegistryType.EXECUTOR.name().equals(registry.getRegistryGroup())) {
										String appName = registry.getRegistryKey();
										List<String> registryList = appAddressMap.get(appName);
										if (CollectionUtil.isEmpty(registryList)) {
											registryList = CollectionUtil.newArrayList();
										}
										if (!registryList.contains(registry.getRegistryValue())) {
											registryList.add(registry.getRegistryValue());
										}
										appAddressMap.put(appName, registryList);
									}
								}
							}

							// fresh group address
							for (JobGroupVO group: jobGroups) {
								List<String> registryList = appAddressMap.get(group.getAppName());
								if (CollectionUtil.isNotEmpty(registryList)) {
									JobGroupDTO jobGroupDTO = BeanUtil.copyProperties(group, JobGroupDTO.class);
									jobGroupDTO.setAddresses(registryList);
									jobGroupService.updateJobGroup(jobGroupDTO);
								}
							}
						}
					} catch (Exception e) {
						if (!toStop) {
							log.error(">>>>>>>>>>> xxl-job, job registry monitor thread error: ", e);
						}
					}
					try {
						TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT.getValue());
					} catch (InterruptedException e) {
						if (!toStop) {
							log.error(">>>>>>>>>>> xxl-job, job registry monitor thread error: ", e);
						}
					}
				}
				log.info(">>>>>>>>>>> xxl-job, job registry monitor thread stop");
			}
		});
		registryMonitorThread.setDaemon(true);
		registryMonitorThread.setName("xxl-job, admin JobRegistryMonitorHelper-registryMonitorThread");
		registryMonitorThread.start();
	}

	@Override
	public void stop(){
		toStop = true;

		// stop registryOrRemoveThreadPool
		registryOrRemoveThreadPool.shutdownNow();

		// stop monitir (interrupt and wait)
		registryMonitorThread.interrupt();
		try {
			registryMonitorThread.join();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void addRegistry(Runnable task) {
		registryOrRemoveThreadPool.execute(task);
	}

	public void unRegistry(Runnable task) {
		registryOrRemoveThreadPool.execute(task);
	}




}
