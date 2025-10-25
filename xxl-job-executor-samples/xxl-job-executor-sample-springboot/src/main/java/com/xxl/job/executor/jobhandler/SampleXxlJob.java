package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.http.http.HttpResponse;
import com.xxl.tool.http.http.enums.ContentType;
import com.xxl.tool.http.http.enums.Header;
import com.xxl.tool.http.http.enums.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);


    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        // default success
    }


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 业务逻辑
        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                XxlJobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }


    /**
     * 3、命令行任务
     *
     *  参数示例："ls -a" 或者 "pwd"
     */
    @XxlJob("commandJobHandler")
    public void commandJobHandler() throws Exception {
        String command = XxlJobHelper.getJobParam();
        int exitValue = -1;

        BufferedReader bufferedReader = null;
        try {
            // valid
            if (command==null || command.trim().length()==0) {
                XxlJobHelper.handleFail("command empty.");
                return;
            }

            // command split
            String[] commandArray = command.split(" ");

            // command process
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(commandArray);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            //Process process = Runtime.getRuntime().exec(command);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

            // command log
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                XxlJobHelper.log(line);
            }

            // command exit
            process.waitFor();
            exitValue = process.exitValue();
        } catch (Exception e) {
            XxlJobHelper.log(e);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        if (exitValue == 0) {
            // default success
        } else {
            XxlJobHelper.handleFail("command exit value("+exitValue+") is failed");
        }

    }


    /**
     * 4、跨平台Http任务
     *
     *  参数示例：
     *  <pre>
     *      // 1、简单示例：
     *      {
     *          "url": "http://www.baidu.com",
     *          "method": "get",
     *          "data": "hello world"
     *      }
     *
     *      // 2、完整参数示例：
     *      {
     *          "url": "http://www.baidu.com",
     *          "method": "POST",
     *          "contentType": "application/json",
     *          "headers": {
     *              "header01": "value01"
     *          },
     *          "cookies": {
     *              "cookie01": "value01"
     *          },
     *          "timeout": 3000,
     *          "data": "request body data",
     *          "form": {
     *              "key01": "value01"
     *          },
     *          "auth": "auth data"
     *      }
     *  </pre>
     */
    @XxlJob("httpJobHandler")
    public void httpJobHandler() throws Exception {

        // param data
        String param = XxlJobHelper.getJobParam();
        if (param==null || param.trim().isEmpty()) {
            XxlJobHelper.log("param["+ param +"] invalid.");

            XxlJobHelper.handleFail();
            return;
        }

        // param parse
        HttpJobParam httpJobParam = null;
        try {
            httpJobParam = GsonTool.fromJson(param, HttpJobParam.class);
        } catch (Exception e) {
            XxlJobHelper.log(new RuntimeException("HttpJobParam parse error", e));
            XxlJobHelper.handleFail();
            return;
        }

        // param valid
        if (httpJobParam == null) {
            XxlJobHelper.log("param parse fail.");
            XxlJobHelper.handleFail();
            return;
        }
        if (StringTool.isBlank(httpJobParam.getUrl())) {
            XxlJobHelper.log("url["+ httpJobParam.getUrl() +"] invalid.");
            XxlJobHelper.handleFail();
            return;
        }
        if (!isValidDomain(httpJobParam.getUrl())) {
            XxlJobHelper.log("url["+ httpJobParam.getUrl() +"] not allowed.");
            XxlJobHelper.handleFail();
            return;
        }
        Method method = Method.POST;
        if (StringTool.isNotBlank(httpJobParam.getMethod())) {
            Method methodParam = Method.valueOf(httpJobParam.getMethod().toUpperCase());
            if (methodParam == null) {
                XxlJobHelper.log("method["+ httpJobParam.getMethod() +"] invalid.");
                XxlJobHelper.handleFail();
                return;
            }
            method = methodParam;
        }
        ContentType contentType = ContentType.JSON;
        if (StringTool.isNotBlank(httpJobParam.getContentType())) {
            for (ContentType contentTypeParam : ContentType.values()) {
                if (contentTypeParam.getValue().equals(httpJobParam.getContentType())) {
                    contentType = contentTypeParam;
                    break;
                }
            }
        }
        if (httpJobParam.getTimeout() <= 0) {
            httpJobParam.setTimeout(3000);
        }

        // do request
        try {
            HttpResponse httpResponse = HttpTool.createRequest()
                    .url(httpJobParam.getUrl())
                    .method(method)
                    .contentType(contentType)
                    .header(httpJobParam.getHeaders())
                    .cookie(httpJobParam.getCookies())
                    .body(httpJobParam.getData())
                    .form(httpJobParam.getForm())
                    .auth(httpJobParam.getAuth())
                    .execute();

            XxlJobHelper.log("StatusCode: " + httpResponse.statusCode());
            XxlJobHelper.log("Response: <br>" + httpResponse.response());
        } catch (Exception e) {
            XxlJobHelper.log(e);
            XxlJobHelper.handleFail();
        }
    }

    /**
     * domain white-list, for httpJobHandler
     */
    private static Set<String> DOMAIN_WHITE_LIST = Set.of(
            "http://www.baidu.com",
            "http://cn.bing.com"
    );

    /**
     * valid if domain is in white-list
     */
    private boolean isValidDomain(String url) {
        if (url == null || DOMAIN_WHITE_LIST.isEmpty()) {
            return false;
        }
        for (String prefix : DOMAIN_WHITE_LIST) {
            if (url.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /*public static void main(String[] args) {
        HttpJobParam httpJobParam = new HttpJobParam();
        httpJobParam.setUrl("http://www.baidu.com");
        httpJobParam.setMethod(Method.POST.name());
        httpJobParam.setContentType(ContentType.JSON.getValue());
        httpJobParam.setHeaders(Map.of("header01", "value01"));
        httpJobParam.setCookies(Map.of("cookie01", "value01"));
        httpJobParam.setTimeout(3000);
        httpJobParam.setData("request body data");
        httpJobParam.setForm(Map.of("form01", "value01"));
        httpJobParam.setAuth("auth data");

        logger.info(GsonTool.toJson(httpJobParam));
    }*/

    /**
     * http job param
     */
    private static class HttpJobParam{
        private String url;                                     // 请求 Url
        private String method;                                  // Method
        private String contentType;                             // Content-Type
        private Map<String, String> headers;                    // 存储请求头
        private Map<String, String> cookies;                    // Cookie（需要格式转换）
        private int timeout;                                    // 请求超时时间
        private String data;                                    // 存储请求体
        private Map<String, String> form;                       // 存储表单数据
        private String auth;                                    // 鉴权信息

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, String> cookies) {
            this.cookies = cookies;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Map<String, String> getForm() {
            return form;
        }

        public void setForm(Map<String, String> form) {
            this.form = form;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }
    }

    /**
     * 5、生命周期任务示例：任务初始化与销毁时，支持自定义相关逻辑；
     */
    @XxlJob(value = "demoJobHandler2", init = "init", destroy = "destroy")
    public void demoJobHandler2() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");
    }
    public void init(){
        logger.info("init");
    }
    public void destroy(){
        logger.info("destroy");
    }


}
