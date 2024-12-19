package com.xxl.job.admin.controller;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm4.Sm4;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.admin.core.conf.XxlJobMailConfig;
import com.xxl.job.admin.security.SecurityContext;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/emailconfig")
public class EmailConfigController {
    private static Logger logger = LoggerFactory.getLogger(EmailConfigController.class);

    @Autowired
    private XxlJobMailConfig xxlJobMailConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping
    public String index(HttpServletRequest request, Model model) throws Exception {
        return "emailconfig/emailconfig.index";
    }

    @RequestMapping("/get")
    @ResponseBody
    public ReturnT<String> get(HttpServletRequest request, String sign, String payload) throws Exception {
        Map<String, String> map = xxlJobMailConfig.getConfig();
        Properties properties = new Properties();
        properties.putAll(map);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        properties.store(bos, null);
        bos.close();
        String content = new String(bos.toByteArray());

        Keypair keypair = SecurityContext.getInstance().findKeypair(sign);

        String secretKey = Sm2.doDecrypt(payload, keypair.getPrivateKey());
        String enc = Sm4.encrypt(content, secretKey);

        return new ReturnT<>(enc);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(HttpServletRequest request, String sign, String payload, String content) throws Exception {
        Keypair keypair = SecurityContext.getInstance().findKeypair(sign);
        String secretKey = Sm2.doDecrypt(payload, keypair.getPrivateKey());
        String dec = Sm4.decrypt(content, secretKey);
        ByteArrayInputStream bis = new ByteArrayInputStream(dec.getBytes("UTF-8"));
        Properties properties = new Properties();
        properties.load(bis);
        bis.close();

        xxlJobMailConfig.loadConfig(properties);

        xxlJobMailConfig.storeConfig();
        return ReturnT.SUCCESS;
    }


}
