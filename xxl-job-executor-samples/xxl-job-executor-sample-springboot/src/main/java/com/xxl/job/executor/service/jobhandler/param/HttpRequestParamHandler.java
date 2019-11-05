package com.xxl.job.executor.service.jobhandler.param;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpMethod.resolve;

@Component
public class HttpRequestParamHandler {
    private static final String HTTP_METHODS = "^(GET|PUT|POST|PATCH|DELETE)";
    private static final String URL = "(https?://[^\\s]+)";
    private static final String REQUEST_BODY = "(\\{.*})";

    public HttpRequestParam convertParam(String param) {
        HttpRequestParam httpRequestParam = new HttpRequestParam();

        Pattern patternOne = Pattern.compile(HTTP_METHODS + " " + URL + " " + REQUEST_BODY);
        Pattern patternTwo = Pattern.compile(HTTP_METHODS + " " + URL);
        Pattern patternThree = Pattern.compile(URL);

        List<Pattern> allowedPatterns = Arrays.asList(patternOne, patternTwo, patternThree);

        Matcher matcher;
        for (Pattern pattern : allowedPatterns) {
            matcher = pattern.matcher(param);
            if (matcher.matches()) {
                switch (matcher.groupCount()) {
                    case 3:
                        httpRequestParam.setHttpMethod(resolve(matcher.group(1)));
                        httpRequestParam.setEndpoint(matcher.group(2));
                        httpRequestParam.setRequestBody(matcher.group(3));
                        break;

                    case 2:
                        httpRequestParam.setHttpMethod(resolve(matcher.group(1)));
                        httpRequestParam.setEndpoint(matcher.group(2));
                        break;

                    case 1:
                        httpRequestParam.setHttpMethod(HttpMethod.GET);
                        httpRequestParam.setEndpoint(matcher.group(1));
                        break;

                    default:
                        throw new RuntimeException("Pattern does not match, please check param format");
                }

            }

        }

        if (httpRequestParam.getHttpMethod() == null) {
            throw new RuntimeException("Pattern does not match, please check param format");
        }

        return httpRequestParam;
    }

}
