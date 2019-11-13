package com.xuxueli.executor.sample.frameless.jobhandler.param;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestParamHandler {
    private static final String HTTP_METHODS = "^(GET|PUT|POST|PATCH|DELETE)";
    private static final String URL = "(https?://[^\\s]+)";
    private static final String REQUEST_BODY = "(\\{.*})";

    private static final HttpRequestParamHandler instance = new HttpRequestParamHandler();

    public static HttpRequestParamHandler getInstance() {
        return instance;
    }

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
                if (matcher.groupCount() == 3) {
                    httpRequestParam.setHttpMethod(matcher.group(1));
                    httpRequestParam.setEndpoint(matcher.group(2));
                    httpRequestParam.setRequestBody(matcher.group(3));
                }
                if (matcher.groupCount() == 2) {
                    httpRequestParam.setHttpMethod(matcher.group(1));
                    httpRequestParam.setEndpoint(matcher.group(2));
                }
                if (matcher.groupCount() == 1) {
                    httpRequestParam.setHttpMethod("GET");
                    httpRequestParam.setEndpoint(matcher.group(1));
                }
            }

        }

        if (httpRequestParam.getHttpMethod() == null) {
            throw new RuntimeException("Pattern does not match, please check param format");
        }

        return httpRequestParam;
    }

}
