package com.xxl.job.executor.service.impl;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhxu.okhttps.HttpResult;
import cn.zhxu.okhttps.HttpUtils;
import cn.zhxu.okhttps.OkHttps;
import com.alibaba.fastjson.JSON;
import com.xxl.job.core.enums.RegistryType;
import com.xxl.job.core.enums.UrlEnum;
import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.core.pojo.dto.RegistryParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.core.utils.ResponseUtils;
import com.xxl.job.core.utils.UrlUtils;
import com.xxl.job.executor.exceptions.XxlJobExecutorException;
import com.xxl.job.executor.service.AdminClient;
import com.xxl.job.spring.boot.autoconfigure.XxlJobExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理客户端实现类
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Service
public class AdminClientImpl implements AdminClient {

    private static final String HOST_STATIC = "%s:%s";

    @Autowired
    private XxlJobExecutorProperties xxlJobExecutorProperties;

    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void callback(List<HandleCallbackParam> callbackParams) {
        List<String> urls = UrlUtils.getUrl(xxlJobExecutorProperties.getAdmin().getAddresses(), UrlEnum.ADMIN_CALLBACK);

        for (String url : urls) {
            try {
                if (ResponseUtils.isSuccess(post(url, callbackParams))) return;
            }catch (Exception e) {
                log.error("<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
            }
        }
        throw new XxlJobExecutorException("job remoting (url=" + JSON.toJSONString(urls) + ")");
    }

    @Override
    public void callback(String address, List<HandleCallbackParam> callbackParams) {
        String url = UrlUtils.getUrl(address, UrlEnum.ADMIN_CALLBACK);
        post(url, callbackParams);
    }

    @Override
    public void registry() {

        List<String> urls = UrlUtils.getUrl(xxlJobExecutorProperties.getAdmin().getAddresses(),UrlEnum.ADMIN_REGISTRY);

        RegistryParam param = new RegistryParam();
        param.setRegistryGroup(RegistryType.EXECUTOR.name());
        param.setRegistryKey(xxlJobExecutorProperties.getExecutor().getAppName());
        param.setRegistryValue(getHost());

        for (String url : urls) {
            try {
                ResponseVO responseVO = post(url, param);
                if (ResponseUtils.isSuccess(responseVO)) {
                    log.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult: {}", param, responseVO);
                    break;
                }
                log.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", param, responseVO);
            } catch (Exception e) {
                log.error(">>>>>>>>>>> xxl-job registry error, registryParam {}, {}", param, e.getMessage());
            }
        }
    }

    @Override
    public void unRegistry() {
        List<String> urls = UrlUtils.getUrl(xxlJobExecutorProperties.getAdmin().getAddresses(), UrlEnum.ADMIN_UNREGISTRY);

        RegistryParam param = new RegistryParam();
        param.setRegistryGroup(RegistryType.EXECUTOR.name());
        param.setRegistryKey(xxlJobExecutorProperties.getExecutor().getAppName());
        param.setRegistryValue(getHost());

        for (String url : urls) {
            try {
                ResponseVO responseVO = post(url, param);
                if (ResponseUtils.isSuccess(responseVO)) {
                    log.debug(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult: {}", param, responseVO);
                    break;
                } else {
                    log.info(">>>>>>>>>>> xxl-job registry-remove fail, url:{}, registryParam:{}, registryResult:{}", url, param, responseVO);
                }
            } catch (Exception e) {
                log.error(">>>>>>>>>>> xxl-job registry-remove error, url:{}, registryParam:{}", url, param, e);
            }
        }
    }

    /**
     * 获取主机
     *
     * @return {@link String}
     */
    private String getHost() {
        Integer port = ObjectUtil.isEmpty(xxlJobExecutorProperties.getExecutor().getPort())
                ? ObjectUtil.isEmpty(serverProperties.getPort()) ? 8080 : serverProperties.getPort()
                : xxlJobExecutorProperties.getExecutor().getPort();

        String host = StrUtil.isEmpty(xxlJobExecutorProperties.getExecutor().getHost())
                ? NetUtil.getLocalhostStr() : xxlJobExecutorProperties.getExecutor().getHost();

        return String.format(HOST_STATIC, host, port);
    }

    /**
     * post
     *
     * @param url   url
     * @param param 参数
     * @return {@link ResponseVO}
     */
    private ResponseVO post(String url, Object param) {
        ResponseVO responseVO = ResponseVO.success();

        HttpResult httpResult = HttpUtils.sync(url)
                .setBodyPara(param)
                .bodyType(OkHttps.JSON)
                .post();
        int status = httpResult.getStatus();
        if (ObjectUtil.equals(200, status)) {
            responseVO = httpResult.getBody()
                    .toBean(ResponseVO.class);
        }else {
            responseVO.setCode(status);
            responseVO.setMessage(ObjectUtil.isNotNull(httpResult.getError()) ? httpResult.getError().getMessage() : null);
        }
        return responseVO;
    }



















}
