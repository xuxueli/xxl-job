
package com.xxl.job.executor.eureka;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class XxlJobEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlJobEurekaApplication.class, args);
    }

}
