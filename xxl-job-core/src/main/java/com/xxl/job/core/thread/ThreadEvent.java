package com.xxl.job.core.thread;

import com.xxl.job.core.enums.ThreadStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 线程事件
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Getter
public class ThreadEvent extends ApplicationEvent {

    private final ThreadStatus threadStatus;

    public ThreadEvent(Object source, ThreadStatus threadStatus) {
        super(source);
        this.threadStatus = threadStatus;
    }

}
