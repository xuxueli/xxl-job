package com.xuxueli.job.client;

import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Luo Bao Ding
 * @since 2019/5/23
 */
@FeignClient(name = "xxl-job-admin", url = "${xxl.job.adminUrl:http://localhost:8080}",
        configuration = XxlJobFeignClientConfiguration.class)
@RequestMapping(path = "/xxl-job-admin/jobops")
public interface XxlJobClient {

    @PostMapping("/add")
    ReturnT<String> add(@RequestBody XxlJobInfo jobInfo);

    @PostMapping("/update")
    ReturnT<String> update(@RequestBody XxlJobInfo jobInfo);

    @PostMapping("/remove")
    ReturnT<String> remove(@RequestParam("uniqName") String uniqName);

    @PostMapping("/stop")
    ReturnT<String> stop(@RequestParam("uniqName") String uniqName);

    @PostMapping("/start")
    ReturnT<String> start(@RequestParam("uniqName") String uniqName);

    @PostMapping("/triggerByUniqName")
    ReturnT<String> triggerByUniqName(@RequestParam("uniqName") String uniqName,
                                      @RequestParam("executorParam") String executorParam);

}
