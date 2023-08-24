package com.xxl.job.admin.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * 遗嘱执行人路由器
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
public abstract class BaseExecutorRouter implements ExecutorRouter {

    @Override
    public String route(RouterParam param) {
        return CollectionUtil.isEmpty(param.getRegistries())
                ? StrUtil.EMPTY : doRoute(param.getJobId(), param.getRegistries());
    }

    /**
     * 路线
     *
     * @param jobId      任务ID
     * @param registries 执行器地址列表
     * @return {@link String}
     */
    protected abstract String doRoute(Long jobId, List<String> registries);


}