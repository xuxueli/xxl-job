package com.xxl.job.executor;

import com.xuxueli.job.client.XxlJobClient;
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

    @RequestMapping("/do")
    public Object doIt(){
        return xxlJobClient.triggerByUniqName("auto_created_job", "");
    }
}
