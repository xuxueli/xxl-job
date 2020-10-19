package com.xuxueli.executor.sample.jfinal;

import com.jfinal.server.undertow.UndertowServer;
import com.xuxueli.executor.sample.jfinal.config.JFinalCoreConfig;

public class XxlJobExecutorApplication {

    public static void main(String[] args) {
        UndertowServer.start(JFinalCoreConfig.class, 8082, true);
    }

}
