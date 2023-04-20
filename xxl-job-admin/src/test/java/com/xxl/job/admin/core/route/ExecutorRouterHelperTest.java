package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExecutorRouterHelperTest {

    @Test
    public void test_LFU() throws InterruptedException {
        XxlJobGroup group = new XxlJobGroup();
        group.setAppname("testname");
        group.setTitle("titletest");
        group.setAddressType(0);
        group.setAddressList("191.0.0.1,192.0.0.2");
        group.setUpdateTime(new Date());

        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1);

        ConcurrentMap<String, AtomicInteger> addressStatsMap = new ConcurrentHashMap<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 100, 0, TimeUnit.HOURS, new LinkedBlockingDeque<>());
        for (int i = 0; i < 1000; i++) {
            threadPoolExecutor.execute(() -> {
                ReturnT<String> route = ExecutorRouteStrategyEnum.FIRST.getRouter().route(group, triggerParam, group.getRegistryList());
                if (route.getCode() == ReturnT.SUCCESS_CODE) {
                    String address = route.getContent();
                    AtomicInteger atomicInteger = addressStatsMap.computeIfAbsent(address, key -> new AtomicInteger(0));
                    atomicInteger.incrementAndGet();
                    //以单个executor为范围，更新address的使用统计
                    ExecutorRouterHelper.updateRouteStats(group.getAppname(), address);
                }
            });
            threadPoolExecutor.execute(() -> {
                ReturnT<String> route = ExecutorRouteStrategyEnum.LEAST_FREQUENTLY_USED.getRouter().route(group, triggerParam, group.getRegistryList());
                if (route.getCode() == ReturnT.SUCCESS_CODE) {
                    String address = route.getContent();
                    AtomicInteger atomicInteger = addressStatsMap.computeIfAbsent(address, key -> new AtomicInteger(0));
                    atomicInteger.incrementAndGet();
                    //以单个executor为范围，更新address的使用统计
                    ExecutorRouterHelper.updateRouteStats(group.getAppname(), address);
                }
            });
        }
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(addressStatsMap.toString());
    }

    @Test
    public void test_LRU() {
        XxlJobGroup group = new XxlJobGroup();
        group.setAppname("testname");
        group.setTitle("titletest");
        group.setAddressType(0);
        group.setAddressList("191.0.0.1,192.0.0.2,193.0.0.3,194.0.0.4,195.0.0.5,196.0.0.6");
        group.setUpdateTime(new Date());

        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1);


        ConcurrentMap<String, AtomicInteger> addressStatsMap = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            ReturnT<String> route = ExecutorRouteStrategyEnum.LEAST_RECENTLY_USED.getRouter().route(group, triggerParam, group.getRegistryList());
            if (route.getCode() == ReturnT.SUCCESS_CODE) {
                String address = route.getContent();
                AtomicInteger atomicInteger = addressStatsMap.computeIfAbsent(address, key -> new AtomicInteger(0));
                atomicInteger.incrementAndGet();
                //以单个executor为范围，更新address的使用统计
                ExecutorRouterHelper.updateRouteStats(group.getAppname(), address);
            }
        }
        System.out.println(addressStatsMap.toString());
    }
}