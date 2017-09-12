package com.xxl.job.executor.service.impl;

import com.xxl.job.executor.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhongdifeng on 2017/8/16.
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
    @Override
    public void helloXXL() {
        logger.info("hello XXL from Route");
    }

    @Override
    public void helloXxlParam(String name) {
        logger.info("============= my name is " + name + " XXL from Route =============");
    }
}
