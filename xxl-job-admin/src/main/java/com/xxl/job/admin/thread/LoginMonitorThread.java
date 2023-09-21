package com.xxl.job.admin.thread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.xxl.job.admin.common.pojo.entity.LoginToken;
import com.xxl.job.admin.service.LoginTokenService;
import com.xxl.job.core.thread.AbstractThreadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 登录监听线程
 * @author Rong.Jia
 * @date 2023/09/09
 */
@Slf4j
@Component
public class LoginMonitorThread extends AbstractThreadListener implements Ordered {

    @Autowired
    private LoginTokenService loginTokenService;

    private Thread monitorThread;
    private volatile boolean toStop = false;

    @Override
    public void start() {
        monitorThread = new Thread(() -> {

            log.info(">>>>>>>>>>> xxl-job, login monitor thread start...");

            // monitor
            while (!toStop) {
                try {
                    List<LoginToken> loginTokens = loginTokenService.list();
                    if (CollectionUtil.isNotEmpty(loginTokens)) {
                        Long now = DateUtil.current();
                        for (LoginToken loginToken : loginTokens) {
                            Integer effectiveDuration = loginToken.getEffectiveDuration();
                            Date updatedTime = loginToken.getUpdatedTime();
                            if (TimeUnit.MILLISECONDS.toSeconds(now - updatedTime.getTime()) > effectiveDuration) {
                                loginTokenService.removeById(loginToken.getId());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(">>>>>>>>>>> xxl-job, login monitor monitor thread error: ", e);
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            log.info(">>>>>>>>>>> xxl-job, login monitor thread stop");

        });
        monitorThread.setDaemon(true);
        monitorThread.setName("xxl-job, admin LoginMonitorHelper");
        monitorThread.start();
    }

    @Override
    public void stop() {
        toStop = true;
        ThreadUtil.interrupt(monitorThread, true);
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
