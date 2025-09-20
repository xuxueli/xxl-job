package com.xxl.tool.freemarker;

import freemarker.core.TemplateClassResolver;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ice2Faith
 * @date 2025/9/20 10:15
 */
public class FtlTool {
    private static final Logger logger = LoggerFactory.getLogger(FtlTool.class);
    private static Configuration freemarkerConfig = null;
    private static BeansWrapper wrapper;

    public FtlTool() {
    }

    public static void init(String templatePath) {
        try {
            freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            freemarkerConfig.setDirectoryForTemplateLoading(new File(templatePath));
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setNumberFormat("0.##########");
            freemarkerConfig.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
            freemarkerConfig.setClassicCompatible(true);
            freemarkerConfig.setLocale(Locale.CHINA);
            freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (IOException var2) {
            IOException e = var2;
            logger.error(e.getMessage(), e);
        }

    }

    public static void init(Configuration freemarkerConfig) {
        FtlTool.freemarkerConfig = freemarkerConfig;
    }

    public static String processTemplateIntoString(Template template, Object model) throws IOException, TemplateException {
        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }

    public static String processString(String templateName, Map<String, Object> params) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName);
        String htmlText = processTemplateIntoString(template, params);
        return htmlText;
    }

    public static String processString(Configuration freemarkerConfig, String templateName, Map<String, Object> params) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName);
        String htmlText = processTemplateIntoString(template, params);
        return htmlText;
    }

    public static TemplateHashModel generateStaticModel(String packageName) {
        try {
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel fileStatics = (TemplateHashModel)staticModels.get(packageName);
            return fileStatics;
        } catch (Exception var3) {
            Exception e = var3;
            throw new IllegalStateException(e);
        }
    }

    static {
        wrapper = (new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)).build();
    }
}

