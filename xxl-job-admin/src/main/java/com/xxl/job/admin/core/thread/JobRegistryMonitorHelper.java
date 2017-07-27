package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import com.xxl.job.core.enums.RegistryConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobRegistryMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorHelper.class);

    private final IXxlJobLogDao xxlJobLogDao;
    private final IXxlJobInfoDao xxlJobInfoDao;
    private final IXxlJobRegistryDao xxlJobRegistryDao;
    private final IXxlJobGroupDao xxlJobGroupDao;

    private boolean toStop = false;

    public void start() {
        Thread registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // auto registry group
                        List<XxlJobGroup> groupList = xxlJobGroupDao.findByAddressType(0);
                        if (CollectionUtils.isNotEmpty(groupList)) {
                            // remove dead address (admin/executor)
                            xxlJobRegistryDao.removeDead(RegistryConfig.DEAD_TIMEOUT);

                            // fresh online address (admin/executor)
                            Map<String, List<String>> appAddressMap = new HashMap<>();
                            List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT);
                            if (list != null) {
                                for (XxlJobRegistry item: list) {
                                    if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                        String appName = item.getRegistryKey();
                                        List<String> registryList = appAddressMap.get(appName);
                                        if (registryList == null) {
                                            registryList = new ArrayList<>();
                                        }

                                        if (!registryList.contains(item.getRegistryValue())) {
                                            registryList.add(item.getRegistryValue());
                                        }
                                        appAddressMap.put(appName, registryList);
                                    }
                                }
                            }

                            // fresh group address
                            for (XxlJobGroup group: groupList) {
                                List<String> registryList = appAddressMap.get(group.getAppName());
                                String addressListStr = StringUtils.join(registryList, ",");

                                group.setAddressList(addressListStr);
                                xxlJobGroupDao.update(group);
                            }

                        }
                    } catch (Exception e) {
                        logger.error("job registry instance error:{}", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        logger.error("job registry instance error:{}", e);
                    }
                }
            }
        });
        registryThread.setDaemon(true);
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
        //registryThread.interrupt();
    }
}
