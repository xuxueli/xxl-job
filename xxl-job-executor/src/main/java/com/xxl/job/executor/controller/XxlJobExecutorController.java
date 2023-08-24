package com.xxl.job.executor.controller;

import com.xxl.job.core.pojo.dto.IdleBeatParam;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.service.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 执行器控制器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/executor")
public class XxlJobExecutorController {

    @Autowired
    private ExecutorService executorService;

    @GetMapping(value = "/beat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO beat() {
        log.debug("接收到beat请求");
        return executorService.beat();
    }

    @GetMapping(value = "/idleBeat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO idleBeat(@Valid IdleBeatParam param) {
        log.debug("接收到idleBeat请求，{}", param.toString());
        return executorService.idleBeat(param);
    }

    @PostMapping(value = "/run", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO run(@RequestBody @Valid TriggerParam param) {
        log.debug("接收到run请求，{}", param.toString());
        return executorService.run(param);
    }

    @DeleteMapping(value = "/kill", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO kill(@RequestBody @Valid KillParam param) {
        log.debug("接收到kill请求，{}", param.toString());
        return executorService.kill(param);
    }

    @GetMapping(value = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<LogResult> log(@Valid LogParam param) {
        log.debug("接收到log请求，{}", param.toString());
        return executorService.log(param);
    }
}
