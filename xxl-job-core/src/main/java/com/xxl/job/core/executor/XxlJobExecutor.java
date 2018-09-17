package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器的核心实现类
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    // ---------------------- param ----------------------
    private String adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath;
    private int logRetentionDays;

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobExecutor.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    // ---------------------- start + stop ----------------------
    /**执行器初始化的时候调用
      * @Description:
      * @author      lixing
      * @param 
      * @return      void
      * @exception   
      * @date        2018/9/17 10:31
      */
    public void start() throws Exception {
        // init admin-client  初始化了调度中心，在本地保存了调度中心的代理（好像是有回调功能可能那个时候会用）
        initAdminBizList(adminAddresses, accessToken);

        // init executor-jobHandlerRepository  初始化执行仓库（执行器名称 与 实例类）
        initJobHandlerRepository(applicationContext);

        // init logpath   日志记录
        XxlJobFileAppender.initLogPath(logPath);

        // init executor-server   初始化执行器的jetty共调度中心调用
        initExecutorServer(port, ip, appName, accessToken);

        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);
    }
    public void destroy(){
        // destory JobThreadRepository
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item: JobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "web container destroy and kill the job.");
            }
            JobThreadRepository.clear();
        }

        // destory executor-server
        stopExecutorServer();

        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();
    }


    // ---------------------- admin-client ----------------------
    private static List<AdminBiz> adminBizList;

    /**
     * 初始化调度中心
     * @param adminAddresses  调度中心地址
     * @param accessToken  通信地址
     * @throws Exception
     */
    private static void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (adminAddresses!=null && adminAddresses.trim().length()>0) {
            for (String address: adminAddresses.trim().split(",")) {
                if (address!=null && address.trim().length()>0) {
                    String addressUrl = address.concat(AdminBiz.MAPPING);
                    AdminBiz adminBiz = (AdminBiz) new NetComClientProxy(AdminBiz.class, addressUrl, accessToken).getObject();
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<AdminBiz>();
                    }
                    //保存远程代理对象
                    adminBizList.add(adminBiz);
                }
            }
        }
    }
    public static List<AdminBiz> getAdminBizList(){
        return adminBizList;
    }


    // ---------------------- executor-server(jetty) ----------------------
    private NetComServerFactory serverFactory = new NetComServerFactory();
    //初始化jetty
    private void initExecutorServer(int port, String ip, String appName, String accessToken) throws Exception {
        // valid param
        port = port>0?port: NetUtil.findAvailablePort(port);

        // start server
        NetComServerFactory.putService(ExecutorBiz.class, new ExecutorBizImpl());   // rpc-service, base on jetty
        //设置调度器的accessToken  执行器要与调度器保持一致
        NetComServerFactory.setAccessToken(accessToken);
        serverFactory.start(port, ip, appName); // jetty + registry
    }
    private void stopExecutorServer() {
        serverFactory.destroy();    // jetty + registry + callback
    }


    // ---------------------- job handler repository ----------------------
    // key :@JobHandler(value="shardingJobHandler")  value:IJobHandler的实例对象
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
    //记录执行器的名称 与 具体对象
    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }
    public static IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }

    /**
     * 执行器 缓存具体执行对象
     * @param applicationContext
     */
    private static void initJobHandlerRepository(ApplicationContext applicationContext){
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler naming conflicts.");
                    }
                    //注册执行器
                    registJobHandler(name, handler);
                }
            }
        }
    }


    // ---------------------- job thread repository ----------------------
    // key：调度中心传过来的任务id   value:执行该任务的线程
    private static ConcurrentHashMap<Integer, JobThread> JobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason){
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }
    public static void removeJobThread(int jobId, String removeOldReason){
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }
    public static JobThread loadJobThread(int jobId){
        JobThread jobThread = JobThreadRepository.get(jobId);
        return jobThread;
    }

}
