package com.xxl.job.admin.util;

import com.xxl.job.admin.core.util.PropertiesUtil;
import org.junit.Test;

/**
 * prop util test
 *
 * @author xuxueli 2017-12-25 15:17:36
 */
public class PropertiesUtilTest {

    @Test
    public void registryTest() throws Exception {
        System.out.println(PropertiesUtil.getString("xxl.job.login.username"));
    }

}
