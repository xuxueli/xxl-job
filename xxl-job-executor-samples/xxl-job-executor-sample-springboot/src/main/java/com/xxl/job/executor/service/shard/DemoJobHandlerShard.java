package com.xxl.job.executor.service.shard;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.shard.SharingHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * demoJobHandlerShard 这个注解必须有 并且要和 demoJobHandler 对应 以Shard结尾
 */
@Service("demoJobHandlerShard")
public class DemoJobHandlerShard implements SharingHandler {

    /**
     * 执行器来确认 分片 *  这里写分片的逻辑 按照时间段 分片，按照文件夹分片等等，分片之后执行器再去执行具体的实现逻辑。
     * @param taskParam 页面传递的参数信息
     * @return
     */
    @Override
    public ReturnT<List<String>> executeShard(String taskParam) {
        ReturnT<List<String>> returnT = new ReturnT<>();
        List<String> strings = Arrays.asList(taskParam.split(","));
        System.out.println("shard");
        returnT.setContent(strings);
        returnT.setCode(ReturnT.SUCCESS_CODE);
        return returnT;
    }
}
