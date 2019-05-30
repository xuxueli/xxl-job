package com.xuxueli.job.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Luo Bao Ding
 * @since 2019/5/30
 */
public class XxlJobClientImpl implements XxlJobClient, DisposableBean {
    public static final String H_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final int HTTP_SUCCESS_CODE = 200;

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
        TypeReference<ReturnT<String>> typeReference = new TypeReference<ReturnT<String>>() {
        };
        reader = objectMapper.readerFor(typeReference);
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) throws IOException {
        String ops = "/add";
        byte[] bytes = writer.writeValueAsBytes(jobInfo);
        HttpEntity entity = new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> update(XxlJobInfo jobInfo) throws IOException {
        String ops = "/update";
        byte[] bytes = writer.writeValueAsBytes(jobInfo);
        HttpEntity entity = new ByteArrayEntity(bytes, ContentType.APPLICATION_JSON);
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> remove(String uniqName) throws IOException {
        String ops = "/remove";
        UrlEncodedFormEntity entity = buildUrlEncodedFormEntity(new BasicNameValuePair("uniqName", uniqName));
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> stop(String uniqName) throws IOException {
        String ops = "/stop";
        UrlEncodedFormEntity entity = buildUrlEncodedFormEntity(new BasicNameValuePair("uniqName", uniqName));
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> start(String uniqName) throws IOException {
        String ops = "/start";
        UrlEncodedFormEntity entity = buildUrlEncodedFormEntity(new BasicNameValuePair("uniqName", uniqName));
        return opsTemplate(ops, entity);
    }

    @Override
    public ReturnT<String> trigger(String uniqName, String executorParam) throws IOException {
        String ops = "/trigger";

        UrlEncodedFormEntity entity = buildUrlEncodedFormEntity(new BasicNameValuePair("uniqName", uniqName),
                new BasicNameValuePair("executorParam", executorParam));
        return opsTemplate(ops, entity);
    }

    private UrlEncodedFormEntity buildUrlEncodedFormEntity(BasicNameValuePair... params) {
        return new UrlEncodedFormEntity(Arrays.asList(params), StandardCharsets.UTF_8);
    }

    @Override
    public void destroy() throws Exception {
        httpClient.close();
    }

    private ReturnT<String> opsTemplate(String ops, HttpEntity entity) throws IOException {
        HttpContext context = HttpClientContext.create();
        HttpPost httpPost = new HttpPost(baseUrl + ops);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader(H_ACCESS_TOKEN, accessToken);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost, context);
        try {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            InputStream content = responseEntity.getContent();

            if (statusCode == HTTP_SUCCESS_CODE) {
                return reader.readValue(content);

            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = content.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }
                String body = os.toString("UTF-8");
                String msg = statusCode + "," + body;
                throw new XxlJobClientException(msg);
            }

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
