package com.xxl.job.admin.service;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.security.ConcurrentLruCache;
import com.xxl.job.admin.core.util.CookieUtil;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Configuration
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "XXL_JOB_LOGIN_IDENTITY";

    public static final String LOGIN_API_TOKEN_HEADER ="token";
    public static final int LOGIN_API_TOKEN_EXPIRE_MINUTES=15;

    public static final String KEY_PAIR_PATH=SecurityContext.STORE_PATH+"/token.pk";

    private final Keypair keypair;
    {
        Keypair pair = SecurityContext.loadStoreKeypair(KEY_PAIR_PATH, null);
        if(pair==null){
            pair=Sm2.generateKeyPairHex();
        }
        SecurityContext.saveStoreKeypair(KEY_PAIR_PATH,pair);
        keypair = pair;
    }

    private ConcurrentLruCache<String, String> cacheParseToken=new ConcurrentLruCache<>(1024, tokenHex->{
        String tokenJson = Sm2.doDecrypt(tokenHex, keypair.getPrivateKey());
        return tokenJson;
    });

    @Resource
    private XxlJobUserDao xxlJobUserDao;


    private String[] makeToken(XxlJobUser xxlJobUser){
        xxlJobUser.setPassword(null);
        String tokenJson = JacksonUtil.writeValueAsString(xxlJobUser);
        long ets=System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(LOGIN_API_TOKEN_EXPIRE_MINUTES);
        String apiToken=Long.toHexString(ets)+"#"+tokenJson;
        String tokenHex = Sm2.doEncrypt(tokenJson, keypair.getPublicKey());
        String apiTokenHex="expire"+ets+"."+Sm2.doEncrypt(apiToken,keypair.getPublicKey());
        return new String[]{tokenHex,apiTokenHex};
    }

    private XxlJobUser parseApiToken(String apiTokenHex){
        XxlJobUser xxlJobUser = null;
        if (apiTokenHex != null) {
            int idx=apiTokenHex.indexOf(".");
            if(idx>=0){
                apiTokenHex=apiTokenHex.substring(idx+1);
            }
            String tokenJson = cacheParseToken.get(apiTokenHex);
            String[] arr = tokenJson.split("#", 2);
            long ts = Long.parseLong(arr[0],16);
            if(ts<System.currentTimeMillis()){
                return null;
            }
            tokenJson=arr[1];
            xxlJobUser = JacksonUtil.readValue(tokenJson, XxlJobUser.class);
        }
        return xxlJobUser;
    }


    private XxlJobUser parseToken(String tokenHex){
        XxlJobUser xxlJobUser = null;
        if (tokenHex != null) {
            String tokenJson = cacheParseToken.get(tokenHex);
            xxlJobUser = JacksonUtil.readValue(tokenJson, XxlJobUser.class);
        }
        return xxlJobUser;
    }


    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember){

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

        String[] tokens = makeToken(xxlJobUser);
        String loginToken=tokens[0];
        String apiToken=tokens[1];

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        response.setHeader(LOGIN_API_TOKEN_HEADER,apiToken);
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
        String token = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        boolean isApiType=false;
        if(!StringUtils.hasText(token)){
            token = request.getHeader(LOGIN_API_TOKEN_HEADER);
            isApiType=true;
        }
        if (StringUtils.hasText(token)) {
            XxlJobUser cookieUser = null;
            try {
                if(isApiType){
                    cookieUser = parseApiToken(token.trim());
                }else{
                    cookieUser = parseToken(token.trim());
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
