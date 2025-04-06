package com.xxl.job.executor.controller;//package com.xxl.job.executor.mvc.controller;

import com.xxl.job.executor.config.XxlJobConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@EnableAutoConfiguration
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping("/")
    @ResponseBody
    String index() {
        return "xxl job executor running.";
    }


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

}