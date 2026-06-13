//package com.xxl.job.core.openapi.client;
//
//import com.xxl.job.core.openapi.ExecutorBiz;
//import com.xxl.job.core.openapi.model.*;
//import com.xxl.job.core.util.XxlJobRemotingUtil;
//import com.xxl.tool.response.Response;
//
///**
// * admin api test
// *
// * @author xuxueli 2017-07-28 22:14:52
// */
//public class ExecutorBizClient implements ExecutorBiz {
//
//    public ExecutorBizClient() {
//    }
//    public ExecutorBizClient(String addressUrl, String accessToken, int timeout) {
//        this.addressUrl = addressUrl;
//        this.accessToken = accessToken;
//        this.timeout = timeout;
//
//        // valid
//        if (!this.addressUrl.endsWith("/")) {
//            this.addressUrl = this.addressUrl + "/";
//        }
//        if (!(this.timeout >=1 && this.timeout <= 10)) {
//            this.timeout = 3;
//        }
//    }
//
//    private String addressUrl ;
//    private String accessToken;
//    private int timeout;
//
//
//    @Override
//    public Response<String> beat() {
//        return XxlJobRemotingUtil.postBody(addressUrl+"beat", accessToken, timeout, "", String.class);
//    }
//
//    @Override
//    public Response<String> idleBeat(IdleBeatRequest idleBeatRequest){
//        return XxlJobRemotingUtil.postBody(addressUrl+"idleBeat", accessToken, timeout, idleBeatRequest, String.class);
//    }
//
//    @Override
//    public Response<String> run(TriggerRequest triggerRequest) {
//        return XxlJobRemotingUtil.postBody(addressUrl + "run", accessToken, timeout, triggerRequest, String.class);
//    }
//
//    @Override
//    public Response<String> kill(KillRequest killRequest) {
//        return XxlJobRemotingUtil.postBody(addressUrl + "kill", accessToken, timeout, killRequest, String.class);
//    }
//
//    @Override
//    public Response<LogResult> log(LogRequest logRequest) {
//        return XxlJobRemotingUtil.postBody(addressUrl + "log", accessToken, timeout, logRequest, LogResult.class);
//    }
//
//}
