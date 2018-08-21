package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.service.impl.JobUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Job4Dispose implements InitializingBean,DisposableBean {

    @Resource
    RedisTemplate<String, ConcurrentHashMap<Integer,List<Integer>>> redisTemplate;


    @Override
    public void destroy() throws Exception {
        updateLeftChildSummarys();
    }


    /**
     * 在停机前清理剩余的日志的childSummarys
     */
    public void updateLeftChildSummarys(){
        for(Map.Entry<Integer, List<Integer>> entry : JobUtils.parentIdChildMap.entrySet()){
            List<Integer> list=entry.getValue();
            int left=list.size();
            if(left==0){
                continue;
            }
            XxlJobDynamicScheduler.adminBiz.updateChildSummaryByParentId(entry.getKey());
        }
        flushToCache();
    }

    public void flushToCache(){
        redisTemplate.opsForValue().set("childJobParentIdMap",JobUtils.childJobParentIdMap);
        redisTemplate.opsForValue().set("parentIdChildMap",JobUtils.parentIdChildMap);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ConcurrentHashMap<Integer,List<Integer>> map=redisTemplate.opsForValue().get("parentIdChildMap");
        if(map!=null){
            JobUtils.parentIdChildMap.putAll(map);
        }

        map=redisTemplate.opsForValue().get("childJobParentIdMap");
        if(map!=null){
            JobUtils.childJobParentIdMap.putAll(map);
        }
    }
}
