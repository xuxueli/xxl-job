package com.xxl.job.core.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @date 2018-10-12 22:51
 * @email sjdong3@iflytek.com
 */
public class NetUtilTest {
    private Logger logger = LoggerFactory.getLogger(NetUtilTest.class);

    @Test
    public void findAvailablePort() {
        logger.debug("port = {}", NetUtil.findAvailablePort(9999));
    }

    @Test
    public void findAvailablePort1() throws IOException {
        ServerSocket socket = new ServerSocket(9999);
        logger.debug("port = {}", NetUtil.findAvailablePort(9999));
    }

    @Test
    public void findAvailablePort2() {
        logger.debug("port = {}", NetUtil.findAvailablePort(1024));
    }
}