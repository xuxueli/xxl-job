package com.xxl.job.executor.test.ollama;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class OllamaTest {
    private static final Logger logger = LoggerFactory.getLogger(OllamaTest.class);

    // ignore
    @MockitoBean
    private XxlJobSpringExecutor xxlJobSpringExecutor;


    @Resource
    private OllamaChatModel ollamaChatModel;

    @Test
    public void chatTest() {

        String model = "qwen3:0.6b";
        String prompt = "背景说明：你是一个研发工程师，擅长解决技术类问题。";
        String input = "请写一个java程序，实现一个方法，输入一个字符串，返回字符串的长度。";


        // build chat-client
        ChatClient ollamaChatClient = ChatClient
                .builder(ollamaChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .defaultOptions(OllamaChatOptions.builder().model(model).build())
                .build();

        // call ollama
        String response = ollamaChatClient
                .prompt(prompt)
                .user(input)
                .call()
                .content();

        logger.info("input: {}", input);
        logger.info("response: {}", response);
    }

    @Test
    public void chatStreamTest() throws InterruptedException {

        String model = "qwen3:0.6b";
        String prompt = "背景说明：你是一个研发工程师，擅长解决技术类问题。";
        String input = "请写一个java程序，实现一个方法，输入一个字符串，返回字符串的长度。";


        // build chat-client
        ChatClient ollamaChatClient = ChatClient
                .builder(ollamaChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .defaultOptions(OllamaChatOptions.builder().model(model).build())
                .build();

        // call ollama
        logger.info("input: {}", input);
        Flux<String> flux = ollamaChatClient
                .prompt(prompt)
                .user(input)
                .stream()
                .content();

        flux.subscribe(
                data -> System.out.println("Received: " + data),  // onNext 处理
                error -> System.err.println("Error: " + error),   // onError 处理
                () -> System.out.println("Completed")             // onComplete 处理
        );

        TimeUnit.SECONDS.sleep(10);

    }


}
