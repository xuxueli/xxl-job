package com.xxl.job.core.thread;

import com.xxl.job.core.enums.ThreadStatus;
import org.springframework.context.ApplicationListener;

/**
 * 线程抽象类
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
public abstract class AbstractThreadListener implements ApplicationListener<ThreadEvent> {

    @Override
    public void onApplicationEvent(ThreadEvent event) {
        if (ThreadStatus.START.equals(event.getThreadStatus())) {
            this.start();
        }else if (ThreadStatus.STOP.equals(event.getThreadStatus())) {
            this.stop();
        }
    }

    /**
     * 开始
     */
    protected abstract void start();

    /**
     * 停止
     */
    protected abstract void stop();







}
