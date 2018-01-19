package com.xxl.job.executor.register;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Adam on 2018/1/11.
 */
public class WebUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);

    private static final int DEFAULT_TIMEOUT = 60000;

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();

    public static String doPost(String url, String params)
            throws IOException {
        String response = "";
        logger.debug("请求数据：, url: {}, params: {}", url, params);
        try {
            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT)
                            .setConnectTimeout(DEFAULT_TIMEOUT).build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8"); // json 格式传递数据
            httpPost.setConfig(requestConfig);

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            httpPost.setEntity(stringEntity);
            long start = System.currentTimeMillis();
            response = httpclient.execute(httpPost, responseHandler());
            long end = System.currentTimeMillis();
            if (end - start > 1000) {
                logger.warn("push job cost too mush time : {} .  url : {}",
                        (end - start), url);
            }
            logger.info("请求返回结果： {}", response);
            httpPost.abort();
            httpPost.releaseConnection();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return response;

    }

    private static ResponseHandler<String> responseHandler() {

        return new ResponseHandler<String>() {

            @Override
            public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    HttpEntity entity = httpResponse.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
    }

}
