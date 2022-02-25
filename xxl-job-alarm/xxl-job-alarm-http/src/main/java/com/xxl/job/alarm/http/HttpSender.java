package com.xxl.job.alarm.http;

import com.xxl.job.alarm.AlarmConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
public class HttpSender {

    private static final Logger logger = LoggerFactory.getLogger(HttpSender.class);


    private static final String SUCCESS = "success";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.create("text/plain", DEFAULT_CHARSET);

    private final List<Pair<String, String>> headers;
    private final String urls;

    public HttpSender(Properties config) {
        urls = config.getProperty(AlarmConstants.ALARM_TARGET);
        String headersStr = config.getProperty(HttpConstants.HTTP_HEADERS);
        if (StringUtils.isNotBlank(headersStr)) {
            String[] headersSplit = headersStr.split(",");
            headers = Stream.of(headersSplit)
                    .map(header -> {
                        String[] h = header.split("=");
                        return new ImmutablePair<>(h[0], h[1]);
                    })
                    .collect(Collectors.toList());
        } else {
            headers = Collections.emptyList();
        }
    }

    public boolean sendMsg(String message) {
        String[] httpUrls = urls.split(",");
        for (String httpUrl : httpUrls) {
            try {
                HttpPost httpPost = new HttpPost(httpUrl);
                httpPost.setEntity(new StringEntity(message, DEFAULT_CONTENT_TYPE));

                for (Pair<String, String> header : headers) {
                    httpPost.setHeader(header.getLeft(), header.getRight());
                }

                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String resp = EntityUtils.toString(entity, DEFAULT_CHARSET);
                if (!Objects.equals(resp, SUCCESS)) {
                    logger.warn("Http send msg {} success, but return error {}", message, resp);
                }
            } catch (Exception e) {
                logger.error("Http send msg :{} failed", message, e);
            }
        }
        return false;
    }
}
