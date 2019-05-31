package com.xxl.job.executor;

import com.xuxueli.job.client.XxlJobClient;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    public ReturnT<String> trigger() throws IOException {
        return xxlJobClient.trigger("auto_created_job", "");
    }

    @RequestMapping("/start")
    public ReturnT<String> start() throws IOException {
        return xxlJobClient.start("auto_created_job");
    }
    @RequestMapping("/stop")
    public ReturnT<String> stop() throws IOException {
        return xxlJobClient.stop("auto_created_job");
    }


}
