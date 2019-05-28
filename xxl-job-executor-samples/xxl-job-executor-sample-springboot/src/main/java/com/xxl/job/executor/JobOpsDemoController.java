package com.xxl.job.executor;

import com.xuxueli.job.client.XxlJobClient;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Luo Bao Ding
 * @since 2019/5/28
 */
@RestController
public class JobOpsDemoController {
    private final XxlJobClient xxlJobClient;


    public JobOpsDemoController(XxlJobClient xxlJobClient) {
        this.xxlJobClient = xxlJobClient;
    }

    @RequestMapping("/trigger")
    public ReturnT<String> trigger(){
        return xxlJobClient.trigger("auto_created_job", "");
    }

    @RequestMapping("/start")
    public ReturnT<String> start(){
        return xxlJobClient.start("auto_created_job");
    }
    @RequestMapping("/stop")
    public ReturnT<String> stop(){
        return xxlJobClient.stop("auto_created_job");
    }


}
