package com.xxl.job.client.netcom.jetty;

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

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.handler.IJobHandler;
import com.xxl.job.client.handler.annotation.JobHander;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobJettyServer implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobJettyServer.class);

    private int port = 9999;
    public void setPort(int port) {
        this.port = port;
    }

    public void start() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Server server = new Server();
                server.setThreadPool(new ExecutorThreadPool(200, 200, 30000));	// 非阻塞

                // connector
                SelectChannelConnector connector = new SelectChannelConnector();
                connector.setPort(port);
                connector.setMaxIdleTime(30000);
                server.setConnectors(new Connector[] { connector });

                // handler
                HandlerCollection handlerc =new HandlerCollection();
                handlerc.setHandlers(new Handler[]{new XxlJobJettyServerHandler()});
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

    public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		XxlJobJettyServer.applicationContext = applicationContext;
		initJobHandler();
	}
	
	/**
	 * init job handler service
	 */
	public void initJobHandler(){
		Map<String, Object> serviceBeanMap = XxlJobJettyServer.applicationContext.getBeansWithAnnotation(JobHander.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String jobName = serviceBean.getClass().getAnnotation(JobHander.class).jobName();
                if (jobName!=null && jobName.trim().length()>0 && serviceBean instanceof IJobHandler) {
                	IJobHandler handler = (IJobHandler) serviceBean;
                	HandlerRepository.regist(jobName, handler);
				}
            }
        }
	}

}
