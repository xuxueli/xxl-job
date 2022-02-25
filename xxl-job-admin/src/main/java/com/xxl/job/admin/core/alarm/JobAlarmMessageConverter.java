package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 告警消息模板转换
 * 支持使用的dataModel有：jobGroup，jobInfo，jobLog，alarmConfig，i18n
 * <p>
 * Created on 2022/2/24.
 *
 * @author lan
 */
@Component
public class JobAlarmMessageConverter implements ApplicationContextAware {

    @Value("${alarm.message.template:classpath:alarm/templates}")
    private String templatePath;

    private Configuration configuration;

    public void init(ApplicationContext applicationContext) {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setTemplateLoader(new SpringTemplateLoader(applicationContext, templatePath));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        init(applicationContext);
    }

    public String convert(String alarmType, Properties config, XxlJobGroup jobGroup, XxlJobInfo jobInfo, XxlJobLog jobLog) throws Exception {
        Template template = configuration.getTemplate(alarmType + ".ftl");
        Map<String, Object> data = new HashMap<>();
        data.put("jobGroup", jobGroup);
        data.put("jobInfo", jobInfo);
        data.put("jobLog", jobLog);
        data.put("alarmConfig", config);
        data.put("i18n", I18nUtil.loadI18nProp());
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        writer.flush();
        writer.close();
        return writer.toString();
    }
}
