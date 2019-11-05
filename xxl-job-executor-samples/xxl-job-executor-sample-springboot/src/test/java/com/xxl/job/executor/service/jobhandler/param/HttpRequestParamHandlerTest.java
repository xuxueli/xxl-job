package com.xxl.job.executor.service.jobhandler.param;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpRequestParamHandlerTest {
    private HttpRequestParamHandler httpRequestParamHandler;

    @Before
    public void setUp() throws Exception {
        httpRequestParamHandler = new HttpRequestParamHandler();
    }

    @Test
    public void shouldConvertParamForPatternOne() {
        //given
        String param = "POST https://example.com:8082/cron-jobs {\"actionType\": \"create\"}";

        //when

        HttpRequestParam httpRequestParam = httpRequestParamHandler.convertParam(param);


        //then
        assertThat(httpRequestParam.getHttpMethod(), is(HttpMethod.POST));
        assertThat(httpRequestParam.getEndpoint(), is("https://example.com:8082/cron-jobs"));
        assertThat(httpRequestParam.getRequestBody(), is("{\"actionType\": \"create\"}"));
    }

    @Test
    public void shouldConvertParamForPatternTwo() {
        //given
        String param = "PUT http://example.com:8082/cron-jobs";

        //when

        HttpRequestParam httpRequestParam = httpRequestParamHandler.convertParam(param);


        //then
        assertThat(httpRequestParam.getHttpMethod(), is(HttpMethod.PUT));
        assertThat(httpRequestParam.getEndpoint(), is("http://example.com:8082/cron-jobs"));
    }

    @Test
    public void shouldConvertParamForPatternThree() {
        //given
        String param = "https://example.com:8082/cron-jobs";

        //when

        HttpRequestParam httpRequestParam = httpRequestParamHandler.convertParam(param);


        //then
        assertThat(httpRequestParam.getHttpMethod(), is(HttpMethod.GET));
        assertThat(httpRequestParam.getEndpoint(), is("https://example.com:8082/cron-jobs"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenPatternDoesNotSupport() {
        //given
        String param = "POST https://example.com:8082/cron-jobs \"{\"actionType\": \"create\"}\" something else";

        //when

        HttpRequestParam httpRequestParam = httpRequestParamHandler.convertParam(param);
    }
}