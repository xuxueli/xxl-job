package com.xuxueli.job.client.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.xuxueli.job.client.XxlJobClient;
import com.xuxueli.job.client.XxlJobClientException;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
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
    public static final int PORT = 7003;

    private static WireMockServer wireMockServer;

    @Autowired
    private XxlJobClient xxlJobClient;


    @BeforeClass
    public static void before() {
        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
        WireMock.configureFor(PORT);
        wireMockServer.start();
    }

    @AfterClass
    public static void after() {
        wireMockServer.stop();

    }

    @Test
    public void trigger() throws JsonProcessingException {
        String path = "trigger";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.trigger("test_every_second2", "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        testTemplate(path, supplier);

    }

    @Test
    public void triggerAbnormal() throws IOException {
        String path = "trigger";

        stubFor(post(urlEqualTo("/xxl-job-admin/jobops/" + path))
                .willReturn(aResponse().withStatus(400)
                        .withHeader("Content-Type", "text/html")
                        .withBody("<h1>error</h1>")));

        try {
            xxlJobClient.trigger("test_every_second2", "");
        } catch (XxlJobClientException ex) {
            return;
        }
        Assert.fail("should throw XxlJobClientException");

    }

    @Test
    public void add() throws JsonProcessingException {
        String path = "add";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.add(new XxlJobInfo());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        testTemplate(path, supplier);

    }

    @Test
    public void update() throws JsonProcessingException {
        String path = "update";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.update(new XxlJobInfo());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        testTemplate(path, supplier);

    }

    @Test
    public void remove() throws JsonProcessingException {
        String path = "remove";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.remove("xx");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        testTemplate(path, supplier);

    }

    @Test
    public void stop() throws JsonProcessingException {
        String path = "stop";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.stop("xx");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        testTemplate(path, supplier);

    }

    @Test
    public void start() throws JsonProcessingException {
        String path = "start";
        Supplier<ReturnT<String>> supplier = () -> {
            try {
                return xxlJobClient.start("xx");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

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
