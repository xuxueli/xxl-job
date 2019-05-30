package com.xuxueli.job.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Luo Bao Ding
 * @since 2019/5/30
 */
public class XxlJobClientImpl implements XxlJobClient, DisposableBean {
    public static final String H_ACCESS_TOKEN = "ACCESS_TOKEN";

    private final CloseableHttpClient httpClient;

    private final String baseUrl;

    private final ObjectWriter writer;
    private final ObjectReader reader;
    private final String accessToken;


    public XxlJobClientImpl(XxlJobProperties xxlJobProperties) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(100);

        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        String serverAddresses = xxlJobProperties.getServerAddresses();
        accessToken = xxlJobProperties.getAccessToken();
        baseUrl = serverAddresses + "/jobops";
        ObjectMapper objectMapper = new ObjectMapper();
        writer = objectMapper.writer();
        reader = objectMapper.readerFor(ReturnT.class);
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) throws IOException {
        String ops = "/add";
        byte[] bytes = writer.writeValueAsBytes(jobInfo);
        HttpEntity entity = new ByteArrayEntity(bytes);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> update(XxlJobInfo jobInfo) throws IOException {
        String ops = "/update";
        byte[] bytes = writer.writeValueAsBytes(jobInfo);
        HttpEntity entity = new ByteArrayEntity(bytes);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> remove(String uniqName) throws IOException {
        String ops = "/remove";
        String json = "{\"uniqName\":\"" + uniqName + "\"}";
        HttpEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> stop(String uniqName) throws IOException {
        String ops = "/stop";
        String json = "{\"uniqName\":\"" + uniqName + "\"}";
        HttpEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> start(String uniqName) throws IOException {
        String ops = "/start";
        String json = "{\"uniqName\":\"" + uniqName + "\"}";
        HttpEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> trigger(String uniqName, String executorParam) throws IOException {
        String ops = "/trigger";
        String json = "{\"uniqName\":\"" + uniqName + "\"" +
                ",\"executorParam\":\"" + executorParam + "\"}";

        HttpEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        return opsTemplate(ops, entity);
    }

    @Override
    public void destroy() throws Exception {
        httpClient.close();
    }

    private ReturnT<String> opsTemplate(String ops, HttpEntity entity) throws IOException {
        HttpContext context = HttpClientContext.create();
        HttpPost httpPost = new HttpPost(baseUrl + ops);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader(H_ACCESS_TOKEN, accessToken);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost, context);
        try {
            HttpEntity responseEntity = response.getEntity();
            InputStream content = responseEntity.getContent();
            return reader.readValue(content);

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ignored) {

                }
            }
        }
    }


}
