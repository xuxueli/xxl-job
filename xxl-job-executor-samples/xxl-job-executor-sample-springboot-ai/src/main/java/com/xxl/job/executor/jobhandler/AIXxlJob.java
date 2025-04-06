package com.xxl.job.executor.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.GsonTool;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

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
     *  参数示例：
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

        // param parse
        String prompt = "你是一个研发工程师，擅长解决技术类问题。";
        String input;
        try {
            Map<String, String> paramMap =GsonTool.fromJson(param, Map.class);
            if (paramMap.containsKey("prompt")) {
                prompt = paramMap.get("prompt");
            }
            input = paramMap.get("input");
            if (input == null || input.trim().isEmpty()) {
                XxlJobHelper.log("input is empty.");

                XxlJobHelper.handleFail();
                return;
            }
        } catch (Exception e) {
            XxlJobHelper.log(e);
            XxlJobHelper.handleFail();
            return;
        }

        // input
        XxlJobHelper.log("<br><br><b>【Input】: " + input + "</b><br><br>");

        // invoke
        String result = chatClient
                .prompt(prompt)
                .user(input)
                .call()
                .content();
        XxlJobHelper.log("<br><br><b>【Output】: " + result+ "</b><br><br>");
    }

}
