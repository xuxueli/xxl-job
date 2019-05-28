package com.xuxueli.job.client.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.xuxueli.job.client.XxlJobClient;
import com.xuxueli.job.client.XxlJobClientAutoConfiguration;
import com.xuxueli.job.client.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * @author Luo Bao Ding
 * @since 2019/5/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
@EnableAutoConfiguration
public class XxlJobClientTest {

    private static WireMockServer wireMockServer;

    @Configuration
    @Import(XxlJobClientAutoConfiguration.class)
    static class TestConfiguration {
    }

    @Autowired
    private XxlJobClient xxlJobClient;


    @BeforeClass
    public static void before(){
        wireMockServer = new WireMockServer(wireMockConfig().port(7005));
        WireMock.configureFor(7005);
        wireMockServer.start();
    }

    @AfterClass
    public static void after(){
        wireMockServer.stop();

    }
    @Test
    public void trigger() throws JsonProcessingException {
        String path = "trigger";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.trigger("test_every_second2", "");

        testTemplate(path, supplier);

    }

    @Test
    public void add() throws JsonProcessingException {
        String path = "add";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.add(new XxlJobInfo());

        testTemplate(path, supplier);

    }

    @Test
    public void update() throws JsonProcessingException {
        String path = "update";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.update(new XxlJobInfo());

        testTemplate(path, supplier);

    }

    @Test
    public void remove() throws JsonProcessingException {
        String path = "remove";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.remove("xx");

        testTemplate(path, supplier);

    }

    @Test
    public void stop() throws JsonProcessingException {
        String path = "stop";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.stop("xx");

        testTemplate(path, supplier);

    }

    @Test
    public void start() throws JsonProcessingException {
        String path = "start";
        Supplier<ReturnT<String>> supplier = () -> xxlJobClient.start("xx");

        testTemplate(path, supplier);

    }



    private void testTemplate(String path, Supplier<ReturnT<String>> supplier) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(ReturnT.SUCCESS);

        stubFor(post(urlEqualTo("/xxl-job-admin/jobops/" + path))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(bytes)
                ));


        ReturnT<String> returnT = supplier.get();

        Assert.assertEquals(200, returnT.getCode());
    }


}
