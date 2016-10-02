package com.xxl.job.core.executor.jetty;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.router.HandlerRouter;
import com.xxl.job.core.util.IpUtil;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    private int port = 9999;
    private String appName;
    private RegistHelper registHelper;
    public void setPort(int port) {
        this.port = port;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public void setRegistHelper(RegistHelper registHelper) {
        this.registHelper = registHelper;
    }

    // ---------------------------------- job server ------------------------------------
    Server server = null;
    public void start() throws Exception {

        Thread executorTnread = new Thread(new Runnable() {
            @Override
            public void run() {
                server = new Server();
                server.setThreadPool(new ExecutorThreadPool(200, 200, 30000));	// 非阻塞

                // connector
                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setPort(port);
                connector.setMaxIdleTime(30000);
                server.setConnectors(new Connector[] { connector });

                // handler
                HandlerCollection handlerc =new HandlerCollection();
                handlerc.setHandlers(new Handler[]{new XxlJobExecutorHandler()});
                server.setHandler(handlerc);

                try {
                    server.start();
                    logger.info(">>>>>>>>>>>> xxl-job jetty server start success at port:{}.", port);
                    registryBeat();
                    server.join();  // block until thread stopped
                    logger.info(">>>>>>>>>>>> xxl-job jetty server join success at port:{}.", port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executorTnread.setDaemon(true); // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        executorTnread.start();
    }
    
    public void destroy(){
    	if (server!=null) {
    		try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    private void registryBeat(){
        if (registHelper==null && appName==null || appName.trim().length()==0) {
            return;
        }
        Thread registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String address = IpUtil.getIp().concat(":").concat(String.valueOf(port));
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

    // ---------------------------------- init job handler ------------------------------------
    public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		XxlJobExecutor.applicationContext = applicationContext;
		initJobHandler();
	}
	
	/**
	 * init job handler action
	 */
	public void initJobHandler(){
		Map<String, Object> serviceBeanMap = XxlJobExecutor.applicationContext.getBeansWithAnnotation(JobHander.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHander.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    HandlerRouter.registJobHandler(name, handler);
                }
            }
        }
	}

}
