package com.xxl.sso.core.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:37
 */

public class TokenHelper {
    private static ObjectMapper objectMapper=new ObjectMapper(){
        {
            setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT+8")));
            setLocale(Locale.CHINA);
        }
    };
    public static String toJson(Object obj){
        try{
            return objectMapper.writeValueAsString(obj);
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }

    public static<T> T fromJson(String json,Class<T> type){
        try{
            return objectMapper.readValue(json,type);
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }
    public TokenHelper() {
    }

    public static Response<String> generateToken(LoginInfo loginInfo) {
        if (loginInfo != null && StringUtils.hasText(loginInfo.getUserId()) && StringUtils.hasText(loginInfo.getSignature())) {
            LoginInfo loginInfoForToken = new LoginInfo(loginInfo.getUserId(), loginInfo.getSignature());
            String json = toJson(loginInfoForToken);
            String token = Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            return Response.ofSuccess(token);
        } else {
            return Response.ofFail("generateToken fail, invalid loginInfo.");
        }
    }

    public static LoginInfo parseToken(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return null;
            } else {
                String json = new String(Base64.getUrlDecoder().decode(token),StandardCharsets.UTF_8);
                return (LoginInfo)fromJson(json, LoginInfo.class);
            }
        } catch (Exception var2) {
            return null;
        }
    }
}
