package com.xxl.job.executor.test.openclaw;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class OpenClawTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenClawTest.class);

    @Resource
    private OpenAiChatModel openAiChatModel;

    /*ChatModel chatModel = OpenAiChatModel
                .builder()
                .openAiApi(OpenAiApi
                        .builder()
                        .baseUrl(baseUrl)
                        .apiKey( token)
                        .webClientBuilder(WebClient.builder().clientConnector(
                                new ReactorClientHttpConnector(
                                        HttpClient.create()
                                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30 * 1000)
                                                .responseTimeout(Duration.ofMillis(30 * 1000))
                                )
                        ))
                        .build())
                .build();*/

    @Test
    public void test() throws Exception {

        String prompt = "你是一个出游助手，擅长做旅游规划";
        String input = "查看下上海今天得天气，给出出游建议";

        // ChatClient
        ChatClient chatClient = ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();

        // Call LLM: 同步输出
        String response = chatClient
                .prompt(prompt)
                .user(input)
                .call()
                .content();

        logger.info("Input: {}", input);
        logger.info("Output: {}", response);
    }

    @Test
    public void test2() throws Exception {

        String prompt = "你是一个出游助手，擅长做旅游规划";
        String input = "查看下上海今天得天气，给出出游建议";

        // ChatClient
        ChatClient chatClient = ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();

        // Call LLM: 流式输出
        Flux<String> flux = chatClient
                .prompt(prompt)
                .user(input)
                .user(user -> user.text(input).params(Map.of("stream", true)))
                .stream()
                .content();

        flux.subscribe(
                data -> System.out.println("Received: " + data),        // onNext 处理
                error -> System.err.println("Error: " + error),     // onError 处理
                () -> System.out.println("Completed")                         // onComplete 处理
        );

        TimeUnit.SECONDS.sleep(30);
    }

}
