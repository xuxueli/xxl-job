package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.GsonTool;
import io.github.imfangs.dify.client.DifyClientFactory;
import io.github.imfangs.dify.client.DifyWorkflowClient;
import io.github.imfangs.dify.client.enums.ResponseMode;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunRequest;
import io.github.imfangs.dify.client.model.workflow.WorkflowRunResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 任务开发示例
 *
 * @author xuxueli 2025-04-06
 */
@Component
public class AIXxlJob {

    // --------------------------------- ollama chat ---------------------------------

    @Resource
    private OllamaChatModel ollamaChatModel;

    /**
     * 1、ollama Chat任务
     *
     *  参数示例：格式见 OllamaParam
     *  <pre>
     *      {
     *          "input": "{输入信息，必填信息}",
     *          "prompt": "{模型prompt，可选信息}"
     *      }
     *  </pre>
     */
    @XxlJob("ollamaJobHandler")
    public void ollamaJobHandler() {

        // param
        String param = XxlJobHelper.getJobParam();
        if (param==null || param.trim().isEmpty()) {
            XxlJobHelper.log("param is empty.");

            XxlJobHelper.handleFail();
            return;
        }

        // ollama param
        OllamaParam ollamaParam = null;
        try {
            ollamaParam = GsonTool.fromJson(param, OllamaParam.class);
            if (ollamaParam.getPrompt()==null || ollamaParam.getPrompt().isBlank()) {
                ollamaParam.setPrompt("你是一个研发工程师，擅长解决技术类问题。");
            }
            if (ollamaParam.getInput() == null || ollamaParam.getInput().isBlank()) {
                XxlJobHelper.log("input is empty.");

                XxlJobHelper.handleFail();
                return;
            }
            if (ollamaParam.getModel()==null || ollamaParam.getModel().isBlank()) {
                ollamaParam.setModel("qwen3:0.6b");
            }
        } catch (Exception e) {
            XxlJobHelper.log(new RuntimeException("OllamaParam parse error", e));
            XxlJobHelper.handleFail();
            return;
        }

        // input
        XxlJobHelper.log("<br><br><b>【Input】: " + ollamaParam.getInput()+ "</b><br><br>");

        // build chat-client
        ChatClient ollamaChatClient = ChatClient
                .builder(ollamaChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();

        // call ollama
        String response = ollamaChatClient
                .prompt(ollamaParam.getPrompt())
                .user(ollamaParam.getInput())
                .options(OllamaOptions.builder().model(ollamaParam.getModel()).build())
                .call()
                .content();

        XxlJobHelper.log("<br><br><b>【Output】: " + response + "</b><br><br>");
    }

    private static class OllamaParam{
        private String input;
        private String prompt;
        private String model;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }


    // --------------------------------- dify workflow ---------------------------------

    /**
     * 2、dify Workflow任务
     *
     *  参数示例：格式见 DifyParam
     *  <pre>
     *      {
     *          "inputs":{                      // inputs 为dify工作流任务参数；参数不固定，结合各自 workflow 自行定义。
     *              "input":"{用户输入信息}"      // 该参数为示例变量，需要 workflow 的“开始”节点 自定义参数 “input”，可自行调整或删除。
     *          },
     *          "user": "{用户标识，选填}"
     *      }
     *  </pre>
     */
    @XxlJob("difyWorkflowJobHandler")
    public void difyWorkflowJobHandler() throws Exception {

        // param
        String param = XxlJobHelper.getJobParam();
        if (param==null || param.trim().isEmpty()) {
            XxlJobHelper.log("param is empty.");
            XxlJobHelper.handleFail();
            return;
        }

        // param parse
        DifyParam difyParam;
        try {
            difyParam =GsonTool.fromJson(param, DifyParam.class);
            if (difyParam.getInputs() == null) {
                difyParam.setInputs(new HashMap<>());
            }
            if (difyParam.getUser() == null) {
                difyParam.setUser("xxl-job");
            }
            if (difyParam.getBaseUrl()==null || difyParam.getApiKey()==null) {
                XxlJobHelper.log("baseUrl or apiKey invalid.");
                XxlJobHelper.handleFail();
                return;
            }
        } catch (Exception e) {
            XxlJobHelper.log(new RuntimeException("DifyParam parse error", e));
            XxlJobHelper.handleFail();
            return;
        }


        // dify param
        XxlJobHelper.log("<br><br><b>【inputs】: " + difyParam.getInputs() + "</b><br><br>");

        // dify request
        WorkflowRunRequest request = WorkflowRunRequest.builder()
                .inputs(difyParam.getInputs())
                .responseMode(ResponseMode.BLOCKING)
                .user(difyParam.getUser())
                .build();

        // dify invoke
        DifyWorkflowClient workflowClient = DifyClientFactory.createWorkflowClient(difyParam.getBaseUrl(), difyParam.getApiKey());
        WorkflowRunResponse response = workflowClient.runWorkflow(request);

        // response
        XxlJobHelper.log("<br><br><b>【Output】: " + response.getData().getOutputs()+ "</b><br><br>");
    }

    private static class DifyParam{

        /**
         * dify input, 允许传入 Dify App 定义的各变量值
         */
        private Map<String, Object> inputs;

        /**
         * dify user
         */
        private String user;

        /**
         * dify baseUrl
         */
        private String baseUrl;

        /**
         * dify apiKey
         */
        private String apiKey;

        public Map<String, Object> getInputs() {
            return inputs;
        }

        public void setInputs(Map<String, Object> inputs) {
            this.inputs = inputs;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

    }

}
