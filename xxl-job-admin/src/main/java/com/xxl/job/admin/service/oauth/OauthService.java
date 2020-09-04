package com.xxl.job.admin.service.oauth;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.CookieUtil;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;
/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Configuration
public class OauthService {

    @Resource
    private XxlJobUserDao xxlJobUserDao;

    public XxlJobUser login(String username, String password){


        Map<String, String> params = new HashMap<String, String>();
        Map<String, String> headers = new HashMap<String, String>();
        params.put("username",username);
        params.put("password",password);
        params.put("grant_type", "password");
        params.put("client_id", XxlJobAdminConfig.getAdminConfig().getClientId());
        params.put("client_secret", XxlJobAdminConfig.getAdminConfig().getClientSecret());
        params.put("redirect_uri", XxlJobAdminConfig.getAdminConfig().getRedirectUrl());

        String result = HttpUtils.doPost( XxlJobAdminConfig.getAdminConfig().getAccessTokenUrl(), params,headers);
        //
        //System.out.println("返回结果：" + result);
        AccessToken accessToken = GsonTool.fromJson(result,AccessToken.class);

    
        System.out.print("access_token=" + accessToken.access_token);
        System.out.print(",scope=" + accessToken.scope + "\n");
        // 获取用户信息
        headers.put("authorization", "Bearer " + accessToken.access_token);
        String userInfo = HttpUtils.doGet( XxlJobAdminConfig.getAdminConfig().getResourceOwnerDetailUrl() ,headers);
        //System.out.print("user info : " + userInfo);
        OauthUser oauthUser = GsonTool.fromJson(userInfo,OauthUser.class);
        XxlJobUser xxlJobUser = xxlJobUserDao.loadByUserName(oauthUser.username);
        if(xxlJobUser == null){
            xxlJobUser = new XxlJobUser();
            xxlJobUser.setUsername(oauthUser.username);
            xxlJobUser.setRole((oauthUser.is_admin == 1) ? 1:0); 
            xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(userInfo.getBytes()));
            xxlJobUserDao.save(xxlJobUser);
            xxlJobUser = xxlJobUserDao.loadByUserName(oauthUser.username);
        }
        return xxlJobUser;
    }


}