package com.xxl.job.admin.service.impl;

import cn.zhxu.okhttps.HttpUtils;
import cn.zhxu.okhttps.OkHttps;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xxl.job.admin.service.ExecutorClient;
import com.xxl.job.core.enums.UrlEnum;
import com.xxl.job.core.pojo.dto.IdleBeatParam;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.core.utils.ResponseUtils;
import com.xxl.job.core.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 执行器客户端实现类
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Service
public class ExecutorClientImpl implements ExecutorClient {

    @Override
    public Boolean beat(String address) {
        try {
            ResponseVO responseVO = HttpUtils.sync(UrlUtils.getUrl(address, UrlEnum.EXECUTOR_BEAT))
                    .get()
                    .getBody()
                    .toBean(ResponseVO.class);
            return ResponseUtils.isSuccess(responseVO);
        }catch (Exception e){
            log.error("beat {}", e.getMessage());
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean idleBeat(String address, IdleBeatParam idleBeatParam) {
        try {
            ResponseVO responseVO = HttpUtils.sync(UrlUtils.getUrl(address, UrlEnum.EXECUTOR_IDLE_BEAT))
                    .addUrlPara(JSONObject.parseObject(JSON.toJSONString(idleBeatParam), new TypeReference<Map<String, Object>>(){}))
                    .get()
                    .getBody()
                    .toBean(ResponseVO.class);
           return ResponseUtils.isSuccess(responseVO);
        }catch (Exception e){
            log.error("idleBeat {}", e.getMessage());
        }
        return Boolean.FALSE;
    }

    @Override
    public ResponseVO run(String address, TriggerParam param) {
        return HttpUtils.sync(UrlUtils.getUrl(address, UrlEnum.EXECUTOR_RUN))
                .setBodyPara(param)
                .bodyType(OkHttps.JSON)
                .post()
                .getBody()
                .toBean(ResponseVO.class);
    }

    @Override
    public ResponseVO kill(String address, KillParam param) {
        return HttpUtils.sync(UrlUtils.getUrl(address, UrlEnum.EXECUTOR_KILL))
                .setBodyPara(param)
                .bodyType(OkHttps.JSON)
                .delete()
                .getBody()
                .toBean(ResponseVO.class);
    }

    @Override
    public LogResult log(String address, LogParam logParam) {
        try {
            ResponseVO responseVO = HttpUtils.sync(UrlUtils.getUrl(address, UrlEnum.EXECUTOR_LOG))
                    .addUrlPara(JSONObject.parseObject(JSON.toJSONString(logParam), new TypeReference<Map<String, Object>>() {
                    }))
                    .get()
                    .getBody()
                    .toBean(ResponseVO.class);
            return ResponseUtils.getResponse(responseVO, LogResult.class);
        }catch (Exception e) {
            log.error("log {}", e.getMessage());
        }
        return null;
    }
}
