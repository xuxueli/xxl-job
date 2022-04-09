package com.xxl.job.core.executor.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.NetUtil;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XxlJobSpringMvcExecutor extends XxlJobSpringExecutor {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobSpringMvcExecutor.class);

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    protected String contextPath;

    // ---------------------- executor-server (rpc provider) ----------------------
    @Override
    protected void initEmbedServer(String address, String ip, int port, String appName, String accessToken) throws Exception {
        // accessToken
        if (accessToken == null || accessToken.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken is empty. To ensure system security, please set the accessToken.");
        }
        registryController(accessToken);
        // start registry
        logger.info("start registry");
        // fill ip port
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();

        // generate address
        if (address == null || address.trim().length() == 0) {
            // registry-address：default use address to registry , otherwise use ip:port if address is null
            String ip_port_address = IpUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }
        String path = contextPath;
        if (path == null || path.trim().length() == 0) {
            path = "";
        }
        if (!"".equals(path) && address.endsWith("/") && !address.endsWith(path + "/")) {
            address = address + path.substring(1) + "/";
        } else if (!"".equals(path) && !address.endsWith(path)) {
            address = address + path + "/";
        }
        if (!address.endsWith("/")) {
            address = address + "/";
        }
        ExecutorRegistryThread.getInstance().start(appName, address + "xxl-job");
    }

    @Override
    protected void stopEmbedServer() {
        // stop registry
        logger.info("stop registry");
        ExecutorRegistryThread.getInstance().toStop();
    }

    protected void registryController(String accessToken) throws NoSuchFieldException, IllegalAccessException {
        Method[] methods = JobController.class.getDeclaredMethods();
        Field config = RequestMappingHandlerMapping.class.getDeclaredField("config");
        config.setAccessible(true);
        RequestMappingHandlerMapping requestMappingHandlerMapping = getApplicationContext().getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo.BuilderConfiguration configuration = (RequestMappingInfo.BuilderConfiguration) config.get(requestMappingHandlerMapping);
        JobController controller = new JobController();
        controller.setAccessToken(accessToken);
        getApplicationContext().getAutowireCapableBeanFactory().autowireBean(controller);
        for (Method method : methods) {
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (requestMapping == null) {
                continue;
            }
            RequestMappingInfo.Builder builder = RequestMappingInfo.paths(requestMapping.path())
                    .methods(requestMapping.method())
                    .params(requestMapping.params())
                    .headers(requestMapping.headers())
                    .consumes(requestMapping.consumes())
                    .produces(requestMapping.produces())
                    .mappingName(requestMapping.name());
            builder.options(configuration);
            requestMappingHandlerMapping.registerMapping(builder.build(), controller, method);
        }
    }

    public static class JobController {

        private String accessToken;

        private final ExecutorBiz executorBiz = new ExecutorBizImpl();

        @ResponseBody
        @PostMapping("/xxl-job/beat")
        public ReturnT<?> beat(HttpServletRequest request) {
            ReturnT<?> returnT = tokenCheck(request);
            if (returnT != null) {
                return returnT;
            }
            return executorBiz.beat();
        }

        private ReturnT<?> tokenCheck(HttpServletRequest request) {
            String accessTokenReq = request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN);
            if (accessToken != null && accessToken.trim().length() > 0 && !accessToken.equals(accessTokenReq)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
            }
            return null;
        }

        @ResponseBody
        @PostMapping("/xxl-job/idleBeat")
        public ReturnT<?> idleBeat(HttpServletRequest request, @RequestBody IdleBeatParam idleBeatParam) {
            ReturnT<?> returnT = tokenCheck(request);
            if (returnT != null) {
                return returnT;
            }
            return executorBiz.idleBeat(idleBeatParam);
        }

        @ResponseBody
        @PostMapping("/xxl-job/run")
        public ReturnT<?> run(HttpServletRequest request, @RequestBody TriggerParam triggerParam) {
            ReturnT<?> returnT = tokenCheck(request);
            if (returnT != null) {
                return returnT;
            }
            return executorBiz.run(triggerParam);
        }

        @ResponseBody
        @PostMapping("/xxl-job/kill")
        public ReturnT<?> kill(HttpServletRequest request, @RequestBody KillParam killParam) {
            ReturnT<?> returnT = tokenCheck(request);
            if (returnT != null) {
                return returnT;
            }
            return executorBiz.kill(killParam);
        }

        @ResponseBody
        @PostMapping("/xxl-job/log")
        public ReturnT<?> log(HttpServletRequest request, @RequestBody LogParam logParam) {
            ReturnT<?> returnT = tokenCheck(request);
            if (returnT != null) {
                return returnT;
            }
            return executorBiz.log(logParam);
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
