package com.xxl.job.core.rpc.netcom.jetty.server;

import com.xxl.job.core.registry.RegistHelper;
import com.xxl.job.core.util.IpUtil;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * rpc jetty server
 * @author xuxueli 2015-11-19 22:29:03
 */
public class JettyServer {
	private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

	private Server server;

	public void start(final int port, final String ip, final String appName, final RegistHelper registHelper) throws Exception {
		Thread thread = new Thread(new Runnable() {
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
				handlerc.setHandlers(new Handler[]{new JettyServerHandler()});
				server.setHandler(handlerc);

				try {
					server.start();
					logger.info(">>>>>>>>>>>> xxl-job jetty server start success at port:{}.", port);
					executorRegistryBeat(port, ip, appName, registHelper);
					server.join();	// block until thread stopped
					logger.info(">>>>>>>>>>> xxl-rpc server start success, netcon={}, port={}", JettyServer.class.getName(), port);
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					server.destroy();
				}
			}
		});
		thread.setDaemon(true);	// daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
		thread.start();
	}

	public void destroy() {
		if (server != null) {
			try {
				server.destroy();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		logger.info(">>>>>>>>>>> xxl-rpc server destroy success, netcon={}", JettyServer.class.getName());
	}

	/**
	 * registry beat
	 * @param port
	 * @param ip
	 * @param appName
	 * @param registHelper
	 */
	private void executorRegistryBeat(final int port, final String ip, final String appName, final RegistHelper registHelper){
		if (registHelper==null && appName==null || appName.trim().length()==0) {
			return;
		}
		Thread registryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
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

}
