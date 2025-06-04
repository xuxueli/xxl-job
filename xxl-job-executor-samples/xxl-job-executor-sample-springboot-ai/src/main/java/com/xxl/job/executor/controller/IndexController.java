package com.xxl.job.executor.controller;//package com.xxl.job.executor.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.imfangs.dify.client.DifyClientFactory;
import io.github.imfangs.dify.client.DifyWorkflowClient;
import io.github.imfangs.dify.client.callback.WorkflowStreamCallback;
import io.github.imfangs.dify.client.enums.ResponseMode;
import io.github.imfangs.dify.client.event.*;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunRequest;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Controller
@EnableAutoConfiguration
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping("/")
    @ResponseBody
    String index() {
        return "xxl job ai executor running.";
    }


    // --------------------------------- ollama chat ---------------------------------

    @Resource
    private ChatClient chatClient;
    private static String prompt = "你好，你是一个研发工程师，擅长解决技术类问题。";

    /**
     * ChatClient 简单调用
     */
    @GetMapping("/chat/simple")
    @ResponseBody
    public String simpleChat(@RequestParam(value = "input") String input) {
        String result = chatClient
                .prompt(prompt)
                .user(input)
                .call()
                .content();
        System.out.println("result: " + result);
        return result;
    }

    /**
     * ChatClient 流式调用
     */
    @GetMapping("/chat/stream")
    public Flux<String> streamChat(HttpServletResponse response, @RequestParam(value = "input") String input) {
        response.setCharacterEncoding("UTF-8");
        return chatClient
                .prompt(prompt)
                .user(input)
                .stream()
                .content();
    }


    // --------------------------------- dify workflow ---------------------------------

    // dify config sample
    private final String apiKey = "http://localhost/v1";
    private final String baseUrl = "app-OUVgNUOQRIMokfmuJvBJoUTN";

    @GetMapping("/dify/simple")
    @ResponseBody
    public String difySimple(@RequestParam(required = false, value = "input") String input) throws IOException {

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("input", input);

        // request
        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .inputs(inputs)
                .responseMode(ResponseMode.BLOCKING)
                .user("user-123")
                .build();

        // invoke
        DifyWorkflowClient workflowClient = DifyClientFactory.createWorkflowClient(baseUrl, apiKey);
        WorkflowRunResponse response = workflowClient.runWorkflow(request);

        // response
        return write2Json(response.getData().getOutputs());
    }

    private String write2Json(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    @GetMapping( "/dify/stream")
    public Flux<String> difyStream(@RequestParam(required = false, value = "input") String input) {

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("input", input);

        // request
        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .inputs(inputs)
                .responseMode(ResponseMode.STREAMING)
                .user("user-123")
                .build();

        // invoke
        DifyWorkflowClient workflowClient = DifyClientFactory.createWorkflowClient(baseUrl, apiKey);
        return Flux.create(new Consumer<FluxSink<String>>() {
            @Override
            public void accept(FluxSink<String> sink) {
                try {
                    workflowClient.runWorkflowStream(request, new WorkflowStreamCallback() {
                        @Override
                        public void onWorkflowStarted(WorkflowStartedEvent event) {
                            sink.next("工作流开始: " + write2Json(event.getData()));
                        }

                        @Override
                        public void onNodeStarted(NodeStartedEvent event) {
                            sink.next("节点开始: " + write2Json(event.getData()));
                        }

                        @Override
                        public void onNodeFinished(NodeFinishedEvent event) {
                            sink.next("节点完成: " + write2Json(event.getData().getOutputs()));
                        }

                        @Override
                        public void onWorkflowFinished(WorkflowFinishedEvent event) {
                            sink.next("工作流完成: " + write2Json(event.getData().getOutputs()));
                            sink.complete();
                        }

                        @Override
                        public void onError(ErrorEvent event) {
                            sink.error(new RuntimeException(event.getMessage()));
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            sink.error(throwable);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}