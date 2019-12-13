package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.impl.netty_http.server.NettyHttpServer;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.serialize.impl.HessianSerializer;
import com.xxl.rpc.util.IpUtil;
import com.xxl.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor {
	private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

	// ---------------------- param ----------------------
	private String adminAddresses;
	private String appName;
	private String ip;
	private int port;
	private String accessToken;
	private String logPath;
	private int logRetentionDays;
	private int poolCore;
	private int poolMax;

	public XxlJobExecutor setAdminAddresses(String adminAddresses) {
		this.adminAddresses = adminAddresses;
		return this;
	}

	public XxlJobExecutor setAppName(String appName) {
		this.appName = appName;
		return this;
	}

	public XxlJobExecutor setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public XxlJobExecutor setPort(int port) {
		this.port = port;
		return this;
	}

	public XxlJobExecutor setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public XxlJobExecutor setLogPath(String logPath) {
		this.logPath = logPath;
		return this;
	}

	public XxlJobExecutor setLogRetentionDays(int logRetentionDays) {
		this.logRetentionDays = logRetentionDays;
		return this;
	}

	public XxlJobExecutor setPoolCore(int poolCore) {
		this.poolCore = poolCore;
		return this;
	}

	public XxlJobExecutor setPoolMax(int poolMax) {
		this.poolMax = poolMax;
		return this;
	}

	// ---------------------- start + stop ----------------------
	public void start() throws Exception {
		// init logpath
		XxlJobFileAppender.initLogPath(logPath);

		// init invoker, admin-client
		initAdminBizList(adminAddresses, accessToken);

		// init JobLogFileCleanThread
		JobLogFileCleanThread.getInstance().start(logRetentionDays);

		// init TriggerCallbackThread
		TriggerCallbackThread.getInstance().start();

		// init executor-server
		port = port > 0 ? port : NetUtil.findAvailablePort(9999);
		ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();
		initRpcProvider();
	}

	public void destroy() {
		// destory executor-server
		stopRpcProvider();

		// destory jobThreadRepository
		if (jobThreadRepository.size() > 0) {
			for (Map.Entry<Integer, JobThread> item : jobThreadRepository.entrySet()) {
				removeJobThread(item.getKey(), "web container destroy and kill the job.");
			}
			jobThreadRepository.clear();
		}
		jobHandlerRepository.clear();

		// destory JobLogFileCleanThread
		JobLogFileCleanThread.getInstance().toStop();

		// destory TriggerCallbackThread
		TriggerCallbackThread.getInstance().toStop();

	}

	// ---------------------- admin-client (rpc invoker) ----------------------
	private static List<AdminBiz> adminBizList;
	private static Serializer serializer = new HessianSerializer();

	private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
		if (adminAddresses == null || (adminAddresses = adminAddresses.trim()).length() <= 1)
			return;

		for (String address : adminAddresses.split(",")) {
			if (address == null || (address = address.trim()).length() <= 0)
				continue;

			AdminBiz adminBiz = new AdminBizClient(address, accessToken);
			if (adminBizList == null) {
				adminBizList = new ArrayList<AdminBiz>();
			}
			adminBizList.add(adminBiz);
		}
	}

	public static List<AdminBiz> getAdminBizList() {
		return adminBizList;
	}

	public static Serializer getSerializer() {
		return serializer;
	}

	// ---------------------- executor-server (rpc provider) ----------------------
	private XxlRpcProviderFactory xxlRpcProviderFactory = null;

	private void initRpcProvider() throws Exception {
		// init, provider factory
		String address = IpUtil.getIpPort(this.ip, this.port);
		Map<String, String> regPm = new HashMap<String, String>();
		regPm.put("appName", this.appName);
		regPm.put("address", address);

		xxlRpcProviderFactory = new XxlRpcProviderFactory();
		xxlRpcProviderFactory.setIp(this.ip);
		xxlRpcProviderFactory.setPort(this.port);
		xxlRpcProviderFactory.setAccessToken(this.accessToken);
		xxlRpcProviderFactory.setCorePoolSize(this.poolCore <= 0 ? 10 : this.poolCore);
		xxlRpcProviderFactory.setMaxPoolSize(this.poolMax <= 0 ? 100 : this.poolMax);
		xxlRpcProviderFactory.setServer(NettyHttpServer.class);
		xxlRpcProviderFactory.setSerializer(HessianSerializer.class);
		xxlRpcProviderFactory.setServiceRegistry(ExecutorServiceRegistry.class);
		xxlRpcProviderFactory.setServiceRegistryParam(regPm);

		// add services
		xxlRpcProviderFactory.addService(ExecutorBiz.class.getName(), null, new ExecutorBizImpl());

		// start
		xxlRpcProviderFactory.start();

	}

	public static class ExecutorServiceRegistry extends ServiceRegistry {

		@Override
		public void start(Map<String, String> param) {
			// start registry
			ExecutorRegistryThread.getInstance().start(param.get("appName"), param.get("address"));
		}

		@Override
		public void stop() {
			// stop registry
			ExecutorRegistryThread.getInstance().toStop();
		}

		@Override
		public boolean registry(Set<String> keys, String value) {
			return false;
		}

		@Override
		public boolean remove(Set<String> keys, String value) {
			return false;
		}

		@Override
		public Map<String, TreeSet<String>> discovery(Set<String> keys) {
			return null;
		}

		@Override
		public TreeSet<String> discovery(String key) {
			return null;
		}

	}

	private void stopRpcProvider() {
		// stop provider factory
		try {
			xxlRpcProviderFactory.stop();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	// ---------------------- job handler repository ----------------------
	private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();

	public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
		logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
		return jobHandlerRepository.put(name, jobHandler);
	}

	public static IJobHandler loadJobHandler(String name) {
		return jobHandlerRepository.get(name);
	}

	// ---------------------- job thread repository ----------------------
	private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();

	public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
		JobThread newJobThread = new JobThread(jobId, handler);
		newJobThread.start();
		logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

		JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread); // putIfAbsent | oh my god, map's put method return the old value!!!
		if (oldJobThread != null) {
			oldJobThread.toStop(removeOldReason);
			oldJobThread.interrupt();
		}

		return newJobThread;
	}

	public static void removeJobThread(int jobId, String removeOldReason) {
		JobThread oldJobThread = jobThreadRepository.remove(jobId);
		if (oldJobThread != null) {
			oldJobThread.toStop(removeOldReason);
			oldJobThread.interrupt();
		}
	}

	public static JobThread loadJobThread(int jobId) {
		JobThread jobThread = jobThreadRepository.get(jobId);
		return jobThread;
	}

	@Override
	public String toString() {
		return "{ip:" + ip
				+ ", port:" + port
				+ ", appName:" + appName
				+ ", accessToken:" + accessToken
				+ ", adminAddresses:" + adminAddresses
				+ ", poolCore:" + poolCore
				+ ", poolMax:" + poolMax
				+ ", logPath:" + logPath
				+ ", logRetentionDays:" + logRetentionDays
				+ ", xxlRpcProviderFactory:" + xxlRpcProviderFactory
				+ "}";
	}
}
