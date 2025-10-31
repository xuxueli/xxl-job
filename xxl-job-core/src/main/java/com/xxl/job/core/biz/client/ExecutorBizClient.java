package com.xxl.job.core.biz.client;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.XxlJobRemotingUtil;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class ExecutorBizClient implements ExecutorBiz {

    public ExecutorBizClient() {
    }
    public ExecutorBizClient(String addressUrl, String accessToken, int timeout) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;
        this.timeout = timeout;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
        if (!(this.timeout >=1 && this.timeout <= 10)) {
            this.timeout = 3;
        }
    }

    private String addressUrl ;
    private String accessToken;
    private int timeout;


    @Override
    public ReturnT<String> beat() {
        return XxlJobRemotingUtil.postBody(addressUrl+"beat", accessToken, timeout, "", String.class);
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatRequest idleBeatRequest){
        return XxlJobRemotingUtil.postBody(addressUrl+"idleBeat", accessToken, timeout, idleBeatRequest, String.class);
    }

    @Override
    public ReturnT<String> run(TriggerRequest triggerRequest) {
        return XxlJobRemotingUtil.postBody(addressUrl + "run", accessToken, timeout, triggerRequest, String.class);
    }

    @Override
    public ReturnT<String> kill(KillRequest killRequest) {
        return XxlJobRemotingUtil.postBody(addressUrl + "kill", accessToken, timeout, killRequest, String.class);
    }

    @Override
    public ReturnT<LogResult> log(LogRequest logRequest) {
        return XxlJobRemotingUtil.postBody(addressUrl + "log", accessToken, timeout, logRequest, LogResult.class);
    }

}
