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
import org.springframework.beans.factory.annotation.Value;
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

    @Resource
    private ChatClient chatClient;

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
    public void ollamaJobHandler() throws Exception {

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
            if (ollamaParam.getPrompt() == null) {
                ollamaParam.setPrompt("你是一个研发工程师，擅长解决技术类问题。");
            }
            if (ollamaParam.getInput() == null || ollamaParam.getInput().trim().isEmpty()) {
                XxlJobHelper.log("input is empty.");

                XxlJobHelper.handleFail();
                return;
            }
        } catch (Exception e) {
            XxlJobHelper.log(new RuntimeException("OllamaParam parse error", e));
            XxlJobHelper.handleFail();
            return;
        }

        // input
        XxlJobHelper.log("<br><br><b>【Input】: " + ollamaParam.getInput()+ "</b><br><br>");

        // invoke
        String result = chatClient
                .prompt(ollamaParam.getPrompt())
                .user(ollamaParam.getInput())
                .call()
                .content();
        XxlJobHelper.log("<br><br><b>【Output】: " + result+ "</b><br><br>");
    }

    private static class OllamaParam{
        private String input;
        private String prompt;

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
    }



    @Value("${dify.api-key}")
    private String apiKey;
    @Value("${dify.base-url}")
    private String baseUrl;

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
        DifyWorkflowClient workflowClient = DifyClientFactory.createWorkflowClient(baseUrl, apiKey);
        WorkflowRunResponse response = workflowClient.runWorkflow(request);

        // response
        XxlJobHelper.log("<br><br><b>【Output】: " + response.getData().getOutputs()+ "</b><br><br>");
    }

    private static class DifyParam{

        /**
         * 输入参数，允许传入 App 定义的各变量值
         */
        private Map<String, Object> inputs;

        /**
         * 用户标识
         */
        private String user;

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

    }


}
