package com.xxl.job.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */

@SpringBootApplication
public class XxlJobAdminApplication {

    private static Logger logger = LoggerFactory.getLogger(XxlJobAdminApplication.class);


    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplicationBuilder(XxlJobAdminApplication.class).web(true)
                .build();
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        logger.info(
                "\n---------------------------------------------------------------------------------------\n\t"
                        +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}\n\t" +
                        "External: \t{}://{}:{}\n\t" +
                        "\n---------------------------------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

}