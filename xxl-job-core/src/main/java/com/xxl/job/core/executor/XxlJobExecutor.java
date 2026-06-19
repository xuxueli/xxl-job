package com.xxl.job.core.executor;

import com.xxl.job.core.constant.Const;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.openapi.admin.AdminBiz;
import com.xxl.job.core.server.EmbedServer;
import com.xxl.job.core.thread.ExecutorRegistryThreadHelper;
import com.xxl.job.core.thread.JobLogFileCleanThreadHelper;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThreadHelper;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.http.IPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor  {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);


    // ---------------------- instance ----------------------

    private static XxlJobExecutor xxlJobExecutor = null;
    public static XxlJobExecutor getInstance() {
        if (xxlJobExecutor == null) {
            throw new RuntimeException(">>>>>>>>>>> xxl-job load executor instance fail, please initialize it.");
        }
        return xxlJobExecutor;
    }


    // ---------------------- field ----------------------

    private String adminAddresses;                                  // admin address list, such as "http://address" or "http://address01,http://address02"
    private String accessToken;                                     // access token
    private int timeout = 3;                                        // timeout by second, default 3s
    private boolean enabled = true;                                 // executor enable, default true
    private String appname;                                         // executor appname
    private String ip;                                              // executor server-info
    private int port;
    private String address;                                         // executor registry-address: default use address to registry , otherwise use ip:port if address is null
    private String logPath = "/data/applogs/xxl-job/jobhandler";    // executor log-path
    private int logRetentionDays = 30;                              // executor log-retention-days
    private boolean glueEnabled = true;                             // executor glue (non-BEAN) task enable, default true

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }
    public void setGlueEnabled(boolean glueEnabled) {
        this.glueEnabled = glueEnabled;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public String getAppname() {
        return appname;
    }
    public int getPort() {
        return port;
    }
    public String getAddress() {
        return address;
    }
    public boolean getGlueEnabled() {
        return glueEnabled;
    }


    // ---------------------- start + stop ----------------------

    private ExecutorRegistryThreadHelper executorRegistryThreadHelper;
    private JobLogFileCleanThreadHelper jobLogFileCleanThreadHelper;
    private TriggerCallbackThreadHelper triggerCallbackThreadHelper;

    public ExecutorRegistryThreadHelper getExecutorRegistryThreadHelper() {
        return executorRegistryThreadHelper;
    }
    public TriggerCallbackThreadHelper getTriggerCallbackThreadHelper() {
        return triggerCallbackThreadHelper;
    }

    /**
     * start
     */
    public void start() throws Exception {

        // valid enabled
        if (!enabled) {
            logger.info(">>>>>>>>>>> xxl-job executor start fail, enabled:{}", enabled);
            return;
        }

        // valid param
        if (StringTool.isBlank(adminAddresses)) {
            throw new RuntimeException("xxl-job executor adminAddresses empty.");
        }
        if (StringTool.isBlank(appname)) {
            throw new RuntimeException("xxl-job executor appname empty.");
        }

        // bind instance
        xxlJobExecutor = this;

        // init logpath
        XxlJobFileAppender.initLogPath(logPath);

        // init invoker, admin-client
        initAdminBizList();

        // 1、init JobLogFileCleanThread
        jobLogFileCleanThreadHelper = new JobLogFileCleanThreadHelper();
        jobLogFileCleanThreadHelper.start(logRetentionDays);

        // 2、init TriggerCallbackThread
        triggerCallbackThreadHelper = new TriggerCallbackThreadHelper();
        triggerCallbackThreadHelper.start(this);

        // 3、EmbedServer + ExecutorRegistryThreadHelper
        executorRegistryThreadHelper = new ExecutorRegistryThreadHelper();
        startEmbedServer();
    }

    /**
     * destroy
     */
    public void destroy(){
        // 1、destroy executor-server
        stopEmbedServer();

        // destroy jobThreadRepository
        if (MapTool.isNotEmpty(jobThreadRepository)) {

            // 1.1、elegant shutdown wait job finish
            try {
                TimeUnit.SECONDS.sleep(Const.ELEGANT_SHUTDOWN_WAITING_SECONDS);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }

            // 1.2、interupt all job-thread
            for (Map.Entry<Integer, JobThread> item: jobThreadRepository.entrySet()) {
                JobThread oldJobThread = removeJobThread(item.getKey(), "web container destroy and kill the job.");
                // wait for job thread push result to callback queue
                if (oldJobThread != null) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        logger.error(">>>>>>>>>>> xxl-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();

        // 2、destroy TriggerCallbackThread
        triggerCallbackThreadHelper.stop();

        // 3、destroy JobLogFileCleanThread
        jobLogFileCleanThreadHelper.stop();
    }


    // ---------------------- admin-client (rpc invoker) ----------------------

    /**
     * admin-client list
     */
    private final List<AdminBiz> adminBizList = new ArrayList<>();

    /**
     * init adminBizList
     */
    private void initAdminBizList() throws Exception {

        // valid
        if (StringTool.isBlank(adminAddresses)) {
            return;
        }

        // build adminBizList
        for (String address: adminAddresses.trim().split(",")) {
            if (StringTool.isBlank(address)) {
                continue;
            }

            // parse param
            String finalAddress = address.trim();
            finalAddress = finalAddress.endsWith("/") ? (finalAddress + "api") : (finalAddress + "/api");
            int finalTimeout = (timeout >=1 && timeout <= 10)
                    ?timeout
                    :3;

            // build
            AdminBiz adminBiz = HttpTool.createClient()
                    .url(finalAddress)
                    .timeout(finalTimeout * 1000)
                    .header(Const.XXL_JOB_ACCESS_TOKEN, accessToken)
                    .proxy(AdminBiz.class);

            // registry
            adminBizList.add(adminBiz);
        }
    }

    /**
     * get adminBizList
     */
    public List<AdminBiz> getAdminBizList(){
        return adminBizList;
    }


    // ---------------------- executor-server (rpc provider) ----------------------

    private EmbedServer embedServer = null;

    /**
     * start embed server
     */
    private void startEmbedServer() throws Exception {

        // fill ip port
        this.port = this.port>0?this.port: IPTool.getAvailablePort(9999);
        this.ip = StringTool.isNotBlank(this.ip) ? this.ip : IPTool.getIp();

        // generate address
        if (StringTool.isBlank(this.address)) {
            // registry-address：default use address to registry , otherwise use ip:port if address is null
            String ip_port_address = IPTool.toAddressString(ip, port);
            this.address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }

        // accessToken
        if (StringTool.isBlank(this.accessToken)) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken is empty. To ensure system security, please set the accessToken.");
        }

        // start
        embedServer = new EmbedServer();
        embedServer.start(this);
    }

    /**
     * stop embed server
     */
    private void stopEmbedServer() {
        // stop provider factory
        if (embedServer != null) {
            try {
                embedServer.stop(this);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    // ---------------------- job handler repository ----------------------

    /**
     * job handler repository
     */
    private final ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    /**
     * load JobHandler instance by name
     */
    public IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }

    /**
     * registry JobHandler
     */
    public IJobHandler registryJobHandler(String name, IJobHandler jobHandler){
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    /**
     * registry JobHandler for method
     */
    protected void registryJobHandler(XxlJob xxlJob, Object bean, Method executeMethod){
        if (xxlJob == null) {
            return;
        }

        String name = xxlJob.value();
        //make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (name.trim().isEmpty()) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }
        if (loadJobHandler(name) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }

        // execute method
        /*if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
            throw new RuntimeException("xxl-job method-jobhandler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                    "The correct method format like \" public ReturnT<String> execute(String param) \" .");
        }
        if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
            throw new RuntimeException("xxl-job method-jobhandler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                    "The correct method format like \" public ReturnT<String> execute(String param) \" .");
        }*/

        executeMethod.setAccessible(true);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (StringTool.isNotBlank(xxlJob.init())) {
            try {
                initMethod = clazz.getDeclaredMethod(xxlJob.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (StringTool.isNotBlank(xxlJob.destroy())) {
            try {
                destroyMethod = clazz.getDeclaredMethod(xxlJob.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        // registry jobhandler
        registryJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));

    }


    // ---------------------- job thread repository ----------------------

    private final ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<>();
    public JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason){
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job register JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public JobThread removeJobThread(int jobId, String removeOldReason){
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();

            return oldJobThread;
        }
        return null;
    }

    public JobThread loadJobThread(int jobId){
        return jobThreadRepository.get(jobId);
    }

}
