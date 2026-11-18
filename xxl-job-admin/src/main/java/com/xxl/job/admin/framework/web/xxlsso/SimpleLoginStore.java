package com.xxl.job.admin.framework.web.xxlsso;

import com.xxl.job.admin.framework.constant.Consts;
import com.xxl.job.admin.framework.mapper.XxlJobUserMapper;
import com.xxl.job.admin.framework.model.XxlJobUser;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.core.store.LoginStore;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Simple LoginStore
 *
 * 1、store by database；
 * 2、If you have higher performance requirements, it is recommended to use RedisLoginStore；
 *
 * @author xuxueli 2025-08-03
 */
@Component
public class SimpleLoginStore implements LoginStore {


    @Resource
    private XxlJobUserMapper xxlJobUserMapper;


    @Override
    public Response<String> set(LoginInfo loginInfo) {
        if (loginInfo == null) {
            return Response.ofFail("userId invalid.");
        }
        Integer userId = parsePositiveUserId(loginInfo.getUserId());
        if (userId == null) {
            return Response.ofFail("userId invalid.");
        }

        // parse token-signature
        String token_sign = loginInfo.getSignature();

        // write token by UserId
        int ret = xxlJobUserMapper.updateToken(userId, token_sign);
        return ret > 0 ? Response.ofSuccess() : Response.ofFail("token set fail");
    }

    @Override
    public Response<String> update(LoginInfo loginInfo) {
        return Response.ofFail("not support");
    }

    @Override
    public Response<String> remove(String userId) {
        Integer parsedUserId = parsePositiveUserId(userId);
        if (parsedUserId == null) {
            return Response.ofFail("token remove fail");
        }

        // delete token-signature
        int ret = xxlJobUserMapper.updateToken(parsedUserId, "");
        return ret > 0 ? Response.ofSuccess() : Response.ofFail("token remove fail");
    }

    /**
     * check through DB query
     */
    @Override
    public Response<LoginInfo> get(String userId) {
        Integer parsedUserId = parsePositiveUserId(userId);
        if (parsedUserId == null) {
            return Response.ofFail("userId invalid.");
        }

        // load login-user
        XxlJobUser user = xxlJobUserMapper.loadById(parsedUserId);
        if (user == null) {
            return Response.ofFail("userId invalid.");
        }

        // parse role
        List<String> roleList = user.getRole()==1? List.of(Consts.ADMIN_ROLE):null;

        // parse jobGroup permission
        Map<String, String> extraInfo = MapTool.newMap(
                "jobGroups", user.getPermission()
        );

        // build LoginInfo
        LoginInfo loginInfo = new LoginInfo(userId, user.getToken());
        loginInfo.setUserName(user.getUsername());
        loginInfo.setRoleList(roleList);
        loginInfo.setExtraInfo(extraInfo);

        return Response.ofSuccess(loginInfo);
    }

    private static Integer parsePositiveUserId(String userId) {
        if (userId == null || !userId.matches("^[1-9]\\d{0,9}$")) {
            return null;
        }
        long parsed = Long.parseLong(userId);
        return parsed <= Integer.MAX_VALUE ? (int) parsed : null;
    }

}
