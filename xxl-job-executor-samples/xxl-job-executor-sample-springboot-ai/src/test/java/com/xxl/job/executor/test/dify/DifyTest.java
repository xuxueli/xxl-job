package com.xxl.job.executor.test.dify;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import io.github.imfangs.dify.client.DifyClientFactory;
import io.github.imfangs.dify.client.DifyWorkflowClient;
import io.github.imfangs.dify.client.enums.ResponseMode;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunRequest;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

@SpringBootTest
public class DifyTest {
    private static final Logger logger = LoggerFactory.getLogger(DifyTest.class);

    // ignore
    @MockitoBean
    private XxlJobSpringExecutor xxlJobSpringExecutor;

    @Test
    public void test() throws Exception {

        String baseUrl = "https://xx.ai";
        String apiKey = "xx";
        String user = "zhangsan";
        Map<String, Object> inputs = Map.of(
                "input", "请写一个java程序，实现一个方法，输入一个字符串，返回字符串的长度。"
        );

        // dify request
        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .inputs(inputs)
                .responseMode(ResponseMode.BLOCKING)
                .user(user)
                .build();

        // dify invoke
        DifyWorkflowClient workflowClient = DifyClientFactory.createWorkflowClient(baseUrl, apiKey);
        WorkflowRunResponse response = workflowClient.runWorkflow(request);

        // response
        logger.info("input: " + inputs);
        logger.info("output: " + response.getData().getOutputs());
    }

}
