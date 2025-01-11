package com.xxl.job.admin.service.impl;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.CookieUtil;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.security.ConcurrentLruCache;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Service
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "XXL_JOB_LOGIN_IDENTITY";

    public static final String LOGIN_API_TOKEN_HEADER ="token";
    public static final int LOGIN_API_TOKEN_EXPIRE_MINUTES=30;

    public static final String KEY_PAIR_PATH=SecurityContext.STORE_PATH+"/token.pk";

    public static final SecureRandom RANDOM=new SecureRandom();

    private final Keypair keypair;
    {
        Keypair pair = SecurityContext.loadStoreKeypair(KEY_PAIR_PATH, null);
        if(pair==null){
            try {
                pair=Sm2.generateKeyPairHex();
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        SecurityContext.saveStoreKeypair(KEY_PAIR_PATH,pair);
        keypair = pair;
    }

    private ConcurrentLruCache<String, String> cacheParseToken=new ConcurrentLruCache<>(1024, tokenHex->{
        try {
            String tokenJson = Sm2.doDecrypt(tokenHex, keypair.getPrivateKey());
            return tokenJson;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    });

    @Resource
    private XxlJobUserDao xxlJobUserDao;


    // ---------------------- token tool ----------------------

    public String makeSign(String msg) throws ScriptException {
        String sign= Sm3.sm3(msg);
        return sign.substring(0,4)+sign.substring(sign.length()-4);
    }

    public long getExpireMillSeconds(boolean ifRemember){
        return (ifRemember ? TimeUnit.SECONDS.toMillis(CookieUtil.COOKIE_MAX_AGE) : TimeUnit.MINUTES.toMillis(LOGIN_API_TOKEN_EXPIRE_MINUTES));
    }

    public String refreshToken(String payload,boolean ifRemember) throws ScriptException {
        long ets=System.currentTimeMillis()+ getExpireMillSeconds(ifRemember);
        String timestamp=Long.toHexString(ets);
        String content=timestamp+"."+payload;
        String sign= makeSign(keypair.getPrivateKey()+"."+content);
        String token=sign+"."+content;
        return token;
    }

    private String makeToken(XxlJobUser xxlJobUser,boolean ifRemember) throws ScriptException {
        xxlJobUser.setPassword(null);
        String tokenJson = JacksonUtil.writeValueAsString(xxlJobUser);
        String payload = Sm2.doEncrypt(tokenJson, keypair.getPublicKey());
        String token=refreshToken(payload,ifRemember);
        return token;
    }

    public static class TokenUser{
        public String sign;
        public long ets;
        public String payload;
        public XxlJobUser user;
    }


    private TokenUser parseToken(String token) throws ScriptException {
        if(token==null || token.isEmpty()){
            return null;
        }
        String[] arr = token.split("\\.", 3);
        if(arr.length!=3){
            return null;
        }
        String sign=arr[0];
        String timestamp=arr[1];
        String payload=arr[2];
        long ets = Long.parseLong(timestamp, 16);
        if(ets<System.currentTimeMillis()){
            return null;
        }
        String content=timestamp+"."+payload;
        String resign=makeSign(keypair.getPrivateKey()+"."+content);
        if(!resign.equalsIgnoreCase(sign)){
            return null;
        }
        String tokenJson = cacheParseToken.get(payload);
        XxlJobUser user= JacksonUtil.readValue(tokenJson, XxlJobUser.class);

        TokenUser ret=new TokenUser();
        ret.sign=sign;
        ret.ets=ets;
        ret.payload=payload;
        ret.user=user;
        return ret;
    }

    // ---------------------- login tool, with cookie and db ----------------------

    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember) throws ScriptException {

        // param
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)){
            return new ReturnT<String>(500, I18nUtil.getString("login_param_empty"));
        }

        // valid passowrd
        XxlJobUser xxlJobUser = xxlJobUserDao.loadByUserName(username);
        if (xxlJobUser == null) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }
        if (!SecurityContext.getInstance().matchPassword(password,xxlJobUser.getPassword())) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }

        String token = makeToken(xxlJobUser,ifRemember);

        String webToken=(ifRemember?"y":"n")+"."+token;
        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, webToken, ifRemember);
        response.setHeader(LOGIN_API_TOKEN_HEADER,token);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,LOGIN_API_TOKEN_HEADER);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     *
     * @param request
     * @param response
     */
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     *
     * @param request
     * @return
     */
    public XxlJobUser ifLogin(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = CookieUtil.get(request, LOGIN_IDENTITY_KEY);
        String token=null;
        if(cookie!=null){
            token=cookie.getValue();
        }
        if(!StringUtils.hasText(token)){
            token = request.getHeader(LOGIN_API_TOKEN_HEADER);
        }
        if (StringUtils.hasText(token)) {
            XxlJobUser cookieUser = null;
            boolean ifRemember=false;
            int idx=token.indexOf(".");
            if(idx>=0){
                String remember=token.substring(0,idx);
                if("y".equalsIgnoreCase(remember)){
                    ifRemember=true;
                    token=token.substring(idx+1);
                }else if("n".equalsIgnoreCase(remember)){
                    ifRemember=false;
                    token=token.substring(idx+1);
                }
            }
            try {
                TokenUser tokenUser = parseToken(token.trim());
                if(tokenUser!=null){
                    cookieUser=tokenUser.user;
                    long lts = tokenUser.ets - System.currentTimeMillis();
                    long ets = getExpireMillSeconds(ifRemember);
                    double refreshRate=1.0-(lts*1.0/ets)+0.1;
                    if(RANDOM.nextDouble()<refreshRate) {
                        String refreshToken = refreshToken(tokenUser.payload, ifRemember);
                        String refreshWebToken = (ifRemember ? "y" : "n") + "." + refreshToken;
                        CookieUtil.set(response, LOGIN_IDENTITY_KEY, refreshWebToken, ifRemember);
                        response.setHeader(LOGIN_API_TOKEN_HEADER, refreshToken);
                        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, LOGIN_API_TOKEN_HEADER);
                    }
                }
            } catch (Exception e) {
                logout(request, response);
            }
            if (cookieUser != null) {
                XxlJobUser dbUser = xxlJobUserDao.loadByUserName(cookieUser.getUsername());
                if (dbUser != null) {
                    dbUser.setPassword(null);
                    return dbUser;
                }
            }
        }
        return null;
    }


}
