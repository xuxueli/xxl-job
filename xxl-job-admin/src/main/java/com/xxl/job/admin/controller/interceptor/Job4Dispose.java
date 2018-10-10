package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.xxl.job.admin.service.impl.CacheTemplate;
import com.xxl.job.admin.service.impl.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Job4Dispose implements InitializingBean,DisposableBean {


    private static Logger logger = LoggerFactory.getLogger(Job4Dispose.class);


    @Autowired(required = false)
    CacheTemplate cacheTemplate;


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
            logger.info(String.format("更新日志结果:%d[updateLeftChildSummarys]",entry.getKey()));

            XxlJobDynamicScheduler.adminBiz.updateChildSummaryByParentId(entry.getKey());
        }
        flushToCache();
    }

    public void flushToCache(){
        if(cacheTemplate==null){
            return;
        }
        cacheTemplate.set("childJobParentIdMap",JobUtils.childJobParentIdMap);
        cacheTemplate.set("parentIdChildMap",JobUtils.parentIdChildMap);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(cacheTemplate==null){
            return;
        }
        ConcurrentHashMap<Integer,List<Integer>> map= cacheTemplate.get("parentIdChildMap",ConcurrentHashMap.class);
        if(map!=null){
            JobUtils.parentIdChildMap.putAll(map);
        }

        map= cacheTemplate.get("childJobParentIdMap",ConcurrentHashMap.class);
        if(map!=null){
            JobUtils.childJobParentIdMap.putAll(map);
        }
    }
}
