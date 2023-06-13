package com.xxl.job.core.shard;


import com.xxl.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * 处理页面传递参数，分片时需要集成该类，并使用注解将子类注入到容器中
 * @author daiguojun
 */
public interface SharingHandler {

    /**
     * 处理页面参数,将任务分发到不同的节点执行
     * @param  taskParam 页面传递的参数信息
     * @return List<String>
     */
    ReturnT<List<String>> executeShard(String taskParam);


}
