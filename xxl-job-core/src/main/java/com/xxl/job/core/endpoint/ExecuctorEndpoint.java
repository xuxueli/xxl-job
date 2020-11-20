package com.xxl.job.core.endpoint;

import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.IdleBeatParam;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.TriggerParam;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Configuration
@RestControllerEndpoint(id = "xxl-job")
public class ExecuctorEndpoint {
    ExecutorBizImpl executorBiz = new ExecutorBizImpl();

    @PostMapping("/beat")
    public void beat() {
        executorBiz.beat();
    }

    @PostMapping("/run")
    public void run(@RequestBody TriggerParam param) {
        executorBiz.run(param);
    }

    @PostMapping("/kill")
    public void kill(@RequestBody KillParam param) {
        executorBiz.kill(param);
    }

    @PostMapping("/idleBeat")
    public void idleBeat(@RequestBody IdleBeatParam param) {
        executorBiz.idleBeat(param);
    }

    @PostMapping("/log")
    public void log(@RequestBody LogParam param) {
        executorBiz.log(param);
    }
}
