package com.xxl.job.core.controller;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.properties.XxlJobProperties;
import com.xxl.job.core.util.ThrowableUtil;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SongLongKuan
 * @time 2021/2/19 12:04 下午
 */
@RequestMapping("xxlrpc")
@RestController
public class XxlJobHandlerController {

    @Autowired
    private XxlJobProperties xxlJobProperties;

    private static Logger logger = LoggerFactory.getLogger(XxlJobHandlerController.class);

    private ExecutorBiz executorBiz = new ExecutorBizImpl();

    @PostMapping("beat")
    public ReturnT<String> beat() {
        return apply(() -> executorBiz.beat());
    }


    @PostMapping("idleBeat")
    public ReturnT<String> idleBeat(@RequestBody IdleBeatParam idleBeatParam) {
        return apply(() -> executorBiz.idleBeat(idleBeatParam));
    }

    @PostMapping("run")
    public ReturnT<String> run(@RequestBody TriggerParam triggerParam) {
        return apply(() -> executorBiz.run(triggerParam));
    }

    @PostMapping("kill")
    public ReturnT<String> kill(@RequestBody KillParam killParam) {
        return apply(() -> executorBiz.kill(killParam));
    }

    @PostMapping("log")
    public ReturnT<LogResult> log(@RequestBody LogParam logParam) {
        return apply(() -> executorBiz.log(logParam));
    }


    public <T> ReturnT<T> apply(ApplyFunction<T> applyFunction) {
        try {
            //验证token
            if (StringUtils.hasLength(xxlJobProperties.getAccesstoken())) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String accesstokenReq = request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN);
                if (!xxlJobProperties.getAccesstoken().equals(accesstokenReq)) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
                }
            }

            return applyFunction.apply();
        } catch (Exception exception) {
            logger.info("controller error,", exception);
            return new ReturnT<T>(ReturnT.FAIL_CODE, "request error:" + ThrowableUtil.toString(exception));
        }
    }

    /**
     * 回调接口
     *
     * @param <T>
     */
    public interface ApplyFunction<T> {
        ReturnT<T> apply();
    }

}
