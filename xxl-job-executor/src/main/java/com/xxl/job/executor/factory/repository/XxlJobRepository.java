package com.xxl.job.executor.factory.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.executor.annotation.XxlJob;
import com.xxl.job.executor.exceptions.XxlJobExecutorException;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.handler.MethodJobHandler;
import com.xxl.job.executor.factory.thread.JobThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * xxl-job任务执行器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Component
public class XxlJobRepository {

    /**
     * 任务回调队列
     */
    private final LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<>();

    /**
     * 任务队列
     */
    private final ConcurrentMap<Long, JobThread> jobThreadQueue = new ConcurrentHashMap<>();

    /**
     * 任务处理队列
     */
    private final ConcurrentMap<String, JobHandler> jobHandlerQueue = new ConcurrentHashMap<>();

    public void pushCallBack(HandleCallbackParam callback) {
        callBackQueue.add(callback);
    }

    /**
     * 处理回调队列
     *
     * @return {@link HandleCallbackParam}
     * @throws InterruptedException 中断异常
     */
    public HandleCallbackParam takeCallBack() throws InterruptedException {
        return callBackQueue.take();
    }

    /**
     * 清空回调
     *
     * @return {@link List}<{@link HandleCallbackParam}>
     */
    public List<HandleCallbackParam> drainToCallBack() {
        List<HandleCallbackParam> callbackParams = new ArrayList<>();
        callBackQueue.drainTo(callbackParams);
        return callbackParams;
    }

    /**
     * 清空任务
     */
    public void cleanJob() {
        if (CollectionUtil.isNotEmpty(jobThreadQueue)) {
            for (Map.Entry<Long, JobThread> item : jobThreadQueue.entrySet()) {
                JobThread oldJobThread = removeJob(item.getKey(), "web container destroy and kill the job.");
                // wait for job thread push result to callback queue
                if (ObjectUtil.isNotNull(oldJobThread)) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        log.error(">>>>>>>>>>> xxl-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
        }
        jobThreadQueue.clear();
    }

    /**
     * 删除任务
     *
     * @param jobId           工作id
     * @param removeOldReason 删除旧原因
     * @return {@link JobThread}
     */
    public JobThread removeJob(Long jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadQueue.remove(jobId);
        if (ObjectUtil.isNotNull(oldJobThread)) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
            return oldJobThread;
        }
        return null;
    }

    /**
     * 获取任务
     *
     * @param jobId 工作id
     * @return {@link JobThread}
     */
    public JobThread getJob(Long jobId) {
        return jobThreadQueue.get(jobId);
    }

    /**
     * 注册任务
     *
     * @param jobId           工作id
     * @param handler         处理程序
     * @param removeOldReason 删除旧原因
     * @return {@link JobThread}
     */
    public JobThread registerJob(Long jobId, JobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler, this);
        newJobThread.start();
        log.info(">>>>>>>>>>> xxl-job register JobThread success, jobId:{}, handler:{}", jobId, handler);

        // putIfAbsent | oh my god, map's put method return the old value!!!
        JobThread oldJobThread = jobThreadQueue.put(jobId, newJobThread);
        if (ObjectUtil.isNotNull(oldJobThread)) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    /**
     * 获取任务处理程序
     *
     * @param name 名字
     * @return {@link JobHandler}
     */
    public JobHandler getJobHandler(String name){
        return jobHandlerQueue.get(name);
    }

    public JobHandler registerJobHandler(String name, JobHandler jobHandler){
        return jobHandlerQueue.put(name, jobHandler);
    }

    public void registerJobHandler(XxlJob job, Object bean, Method executeMethod){
        if (ObjectUtil.isEmpty(job)) return;

        String name = job.value();
        //make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (StrUtil.isBlank(name.trim())) {
            throw new XxlJobExecutorException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }
        if (ObjectUtil.isNotNull(getJobHandler(name))) {
            throw new XxlJobExecutorException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }

        ReflectUtil.setAccessible(executeMethod);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (StrUtil.isNotBlank(job.init())) {
            try {
                initMethod = clazz.getDeclaredMethod(job.init());
                ReflectUtil.setAccessible(initMethod);
            } catch (NoSuchMethodException e) {
                throw new XxlJobExecutorException("xxl-job method-job-handler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (StrUtil.isNotBlank(job.destroy())) {
            try {
                destroyMethod = clazz.getDeclaredMethod(job.destroy());
                ReflectUtil.setAccessible(destroyMethod);
            } catch (NoSuchMethodException e) {
                throw new XxlJobExecutorException("xxl-job method-job-handler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        // registry job handler
        registerJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));
    }
























}
