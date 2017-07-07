package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.dao.IXxlJobRegistryDao;
import com.xxl.job.core.enums.RegistryConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

    private ConcurrentHashMap<String, List<String>> registMap = new ConcurrentHashMap<>();

    private boolean toStop = false;

    public void start() {
        Thread registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // remove dead admin/executor
                        xxlJobRegistryDao.removeDead(RegistryConfig.DEAD_TIMEOUT);

                        // fresh registry map
                        ConcurrentHashMap<String, List<String>> temp = new ConcurrentHashMap<>();
                        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT);
                        if (list != null) {
                            for (XxlJobRegistry item : list) {
                                String groupKey = makeGroupKey(item.getRegistryGroup(), item.getRegistryKey());
                                List<String> registryList = temp.get(groupKey);
                                if (registryList == null) {
                                    registryList = new ArrayList<>();
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

    private static String makeGroupKey(String registryGroup, String registryKey) {
        return registryGroup.concat("_").concat(registryKey);
    }

    public List<String> discover(String registryGroup, String registryKey) {
        String groupKey = makeGroupKey(registryGroup, registryKey);
        return registMap.get(groupKey);
    }

}
