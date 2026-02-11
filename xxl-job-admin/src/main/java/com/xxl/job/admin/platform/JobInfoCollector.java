package com.xxl.job.admin.platform;

import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ice2Faith
 * @date 2026/1/6 16:10
 * @desc
 */
@Data
@Component
public class JobInfoCollector implements InitializingBean {
    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;

    private static final AtomicBoolean started=new AtomicBoolean(false);
    private static ConcurrentHashMap<Long, XxlJobInfo> newestMap=new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        startCollectorThread();
    }

    public void startCollectorThread(){
        if(started.getAndSet(true)){
            return;
        }
        Thread thread = new Thread(() -> {
            Random random=new Random();
            while (true) {
                try {
                    collect();
                } catch (Exception e) {

                }
                try {
                    // 1-2分钟随机，避免多台调度器的时候出现并发查询的情况发生
                    Thread.sleep(TimeUnit.SECONDS.toMillis(random.nextInt(60)+60));
                } catch (Exception e) {

                }
            }
        });
        thread.setDaemon(true);
        thread.setName("job-info-collector");
        thread.start();
    }

    public void collect() throws Exception {
        List<XxlJobInfo> list = xxlJobInfoMapper.newestJobExecuteStatusList();
        for (XxlJobInfo item : list) {
            int id = item.getId();
            if(id<=0){
                continue;
            }
            newestMap.put((long)id,item);
        }
    }

    public static Map<Long,XxlJobInfo> getNewestMap(){
        return newestMap;
    }

    public static void fillIfAbsentNewestFields(List<XxlJobInfo> list){
        if(list==null || list.isEmpty()){
            return;
        }
        if(newestMap.isEmpty()) {
            return;
        }
        for (XxlJobInfo item : list) {
            if(item==null){
                continue;
            }
            int id = item.getId();
            if(id<=0){
                continue;
            }
            if(item.getNewestLogStatus()==null){
                XxlJobInfo info = newestMap.get((long) id);
                if(info!=null){
                    item.setNewestLogStatus(info.getNewestLogStatus());
                }
            }
            if(item.getNewestTriggerTime()==null){
                XxlJobInfo info = newestMap.get((long) id);
                if(info!=null){
                    item.setNewestTriggerTime(info.getNewestTriggerTime());
                }
            }
        }
    }
}
