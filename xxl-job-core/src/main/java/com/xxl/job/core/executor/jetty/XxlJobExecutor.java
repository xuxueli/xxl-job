package com.xxl.job.core.executor.jetty;

import java.util.Map;

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

import com.xxl.job.core.handler.HandlerRepository;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    private int port = 9999;
    public void setPort(int port) {
        this.port = port;
    }

    // ---------------------------------- job server ------------------------------------
    Server server = null;
    public void start() throws Exception {

        new Thread(new Runnable() {
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
                    server.join();  // block until server ready
                    logger.info(">>>>>>>>>>>> xxl-job jetty server join success at port:{}.", port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

    // ---------------------------------- init job handler ------------------------------------
    public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		XxlJobExecutor.applicationContext = applicationContext;
		initJobHandler();
	}
	
	/**
	 * init job handler service
	 */
	public void initJobHandler(){
		Map<String, Object> serviceBeanMap = XxlJobExecutor.applicationContext.getBeansWithAnnotation(JobHander.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String jobName = serviceBean.getClass().getAnnotation(JobHander.class).name();
                if (jobName!=null && jobName.trim().length()>0 && serviceBean instanceof IJobHandler) {
                	IJobHandler handler = (IJobHandler) serviceBean;
                	HandlerRepository.regist(jobName, handler);
				}
            }
        }
	}

}
