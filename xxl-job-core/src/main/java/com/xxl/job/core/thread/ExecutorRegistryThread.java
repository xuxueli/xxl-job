package com.xxl.job.core.thread;

import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.util.IpUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 17/3/2.
 */
public class ExecutorRegistryThread extends Thread {

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();
    public static ExecutorRegistryThread getInstance(){
        return instance;
    }

    private Thread registryThread;
    private boolean toStop = false;
    public void start(final int port, final String ip, final String appName, final RegistHelper registHelper){
        if (registHelper==null && appName==null || appName.trim().length()==0) {
            return;
        }
        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // generate addredd = ip:port
                        String address = null;
                        if (ip != null && ip.trim().length()>0) {
                            address = ip.trim().concat(":").concat(String.valueOf(port));
                        } else {
                            address = IpUtil.getIpPort(port);
                        }

                        registHelper.registry(RegistHelper.RegistType.EXECUTOR.name(), appName, address);
                        TimeUnit.SECONDS.sleep(RegistHelper.TIMEOUT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        registryThread.setDaemon(true);
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
    }

}
