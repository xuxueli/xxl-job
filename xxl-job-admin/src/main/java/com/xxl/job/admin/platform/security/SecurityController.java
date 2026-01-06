package com.xxl.job.admin.platform.security;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.sso.core.annotation.XxlSso;
import com.xxl.tool.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptException;

/**
 * @author Ice2Faith
 * @date 2025/12/28 16:37
 * @desc
 */
@RestController
public class SecurityController {

    @RequestMapping(value = "spk", method = RequestMethod.POST)
    @ResponseBody
    @XxlSso(login = false)
    public Response<String> getServerPublicKey(HttpServletRequest request, HttpServletResponse response,
                                               @RequestParam("pk") String pk,
                                               @RequestParam("sign") String sign) throws ScriptException {
        if (!StringUtils.hasLength(pk) || !StringUtils.hasLength(sign)) {
            return Response.ofFail(I18nUtil.getString("system_fail"));
        }
        if (!Sm3.sm3(pk).equals(sign)) {
            return Response.ofFail(I18nUtil.getString("system_fail"));
        }
        Keypair keypair = SecurityContext.getInstance().currentKeypair();
        String publicKey = keypair.getPublicKey();
        String ret = Sm2.doEncrypt(publicKey, pk);
        return Response.ofSuccess(ret);
    }
}
