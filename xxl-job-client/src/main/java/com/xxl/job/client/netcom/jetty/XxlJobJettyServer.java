package com.xxl.job.client.netcom.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobJettyServer {
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

}
