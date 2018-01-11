package com.xxl.job.executor.register;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2018/1/9.
 */
@Component
public class XxlJobRegistor implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobRegistor.class);

    @Autowired
    private List<IJobHandler> jobHandlers;

    @Value("${xxl.job.executor.appname}")
    private String jobExecutorName;

    @Value("${xxl.registor.principal}")
    private String responsible;

    @Value("${xxl.registor.alarm.email}")
    private String alarmEmail;

    @Value("${xxl.job.admin.addresses}")
    private String baseUrl;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Override
    public void afterPropertiesSet() throws Exception {
//        new Thread(new RegisterThread(jobHandlers)).start();
        new RegisterThread(jobHandlers).run();//同步
    }

    private class RegisterThread implements Runnable {

        private List<IJobHandler> jobHandlers;

        public RegisterThread(List<IJobHandler> jobHandlers) {
            this.jobHandlers = jobHandlers;
        }

        public void run() {
            logger.info("开始注册Job....");
            for (IJobHandler jobHandler : jobHandlers) {
                Class clazz = jobHandler.getClass();
                JobAutoRegister jobAutoRegister = AnnotationUtils.getAnnotation(clazz, JobAutoRegister.class);
                if (jobAutoRegister == null)
                    return;

                JobHandler handler = AnnotationUtils.getAnnotation(clazz, JobHandler.class);
                if (handler == null)
                    return;

                logger.info("注册Job: {}", clazz);
                String message = register(jobAutoRegister, handler);
                if (StringUtils.isEmpty(message)) {
                    logger.error("注册Job{}失败! 没有信息!", clazz);
                } else {
                    Map<String, Object> map = JSON.parseObject(message, Map.class);
                    String code = String.valueOf(map.get("code"));
                    if ("200".equals(code)) {
                        logger.info("注册Job: {} 成功", clazz);
                    } else {
                        logger.error("注册Job: {} 失败! {}", clazz, message);
                    }
                }
            }
        }

        private String register(JobAutoRegister jobAutoRegister, JobHandler jobHandler) {
            Map<String, String> map = new HashMap<>();
            map.put("jobGroupAppName", jobExecutorName);
            map.put("jobName", jobAutoRegister.name().concat("-").concat(activeProfiles));
            map.put("jobDesc", jobAutoRegister.desc());
            map.put("executorRouteStrategy", jobAutoRegister.routerPolicy().name());
            map.put("jobCron", jobAutoRegister.cron());
            map.put("glueType", jobAutoRegister.mode());
            map.put("executorHandler", jobHandler.value());
            map.put("executorParam", jobAutoRegister.param());
            map.put("executorBlockStrategy", jobAutoRegister.executePolicy().name());
            map.put("executorFailStrategy", jobAutoRegister.failPolicy().name());
            map.put("author", responsible);
            map.put("alarmEmail", alarmEmail);
            try {
                String message = WebUtil.doPost(baseUrl + "/internal/jobinfo/add", JSON.toJSONString(map));
                return message;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return null;
        }
    }

}
