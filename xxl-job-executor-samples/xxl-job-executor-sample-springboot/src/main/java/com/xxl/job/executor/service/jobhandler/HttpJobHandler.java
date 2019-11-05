package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.service.jobhandler.param.HttpRequestParam;
import com.xxl.job.executor.service.jobhandler.param.HttpRequestParamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value = "httpJobHandler")
@Component
public class HttpJobHandler extends IJobHandler {

    private RestTemplate restTemplate;
    private HttpRequestParamHandler httpRequestParamHandler;

    @Autowired
    public HttpJobHandler(final RestTemplate restTemplate, final HttpRequestParamHandler httpRequestParamHandler) {
        this.restTemplate = restTemplate;
        this.httpRequestParamHandler = httpRequestParamHandler;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        try {
            HttpRequestParam httpRequestParam = httpRequestParamHandler.convertParam(param);
            HttpEntity<String> requestEntity = new HttpEntity<>(httpRequestParam.getRequestBody(), null);
            ResponseEntity<String> exchange = this.restTemplate.exchange(httpRequestParam.getEndpoint(), httpRequestParam.getHttpMethod(), requestEntity, String.class);
            HttpStatus statusCode = exchange.getStatusCode();

            if (HttpStatus.OK != statusCode && HttpStatus.CREATED != statusCode) {
                throw new RuntimeException("Http Request StatusCode (" + statusCode + ") Invalid.");
            }

            XxlJobLogger.log(exchange.toString());
        } catch (Exception ex) {
            XxlJobLogger.log(ex);
            return FAIL;
        }
        return ReturnT.SUCCESS;
    }

}
