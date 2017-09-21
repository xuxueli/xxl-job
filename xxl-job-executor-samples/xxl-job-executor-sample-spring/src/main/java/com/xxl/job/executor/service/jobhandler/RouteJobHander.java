package com.xxl.job.executor.service.jobhandler;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 路由执行指定的任务
 * 第一个参数为beanId+方法名称
 *
 * @author zhongdifeng
 */
@JobHander(value = "routeJobHander")
@Service
public class RouteJobHander extends IJobHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouteJobHander.class);

    /**
     * 根据参数执行相应的方法
     * 第一个参数为beanId+方法名称(例：orderyPay.updateMoney,参数)
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        logger.info("job执行器接受到的参数为：" + params[0]);
        String[] mparam = removeFirst(params);
        String beanId = params[0].split("\\.")[0];
        String methodName = params[0].split("\\.")[1];
        Object obj = XxlJobExecutor.getApplicationContext().getBean(beanId);

        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                m.invoke(obj, mparam);
                return ReturnT.SUCCESS;
            }
        }
        throw new Exception("未找到需要执行的方法：接受方法--" + methodName + "。所有方法:" + methods.toString());
    }


    /**
     * 移除第一个数组元素
     *
     * @param arys
     * @return
     */

    private String[] removeFirst(String[] arys) {
        if (arys.length > 1) {
            String[] rst = new String[arys.length - 1];
            for (int i = 0; i < rst.length; i++) {
                rst[i] = arys[i + 1].trim();
            }
            return rst;
        } else {
            return null;
        }

    }

}
