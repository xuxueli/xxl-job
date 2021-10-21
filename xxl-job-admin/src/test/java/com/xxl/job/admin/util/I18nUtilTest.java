package com.xxl.job.admin.util;

import com.xxl.job.admin.core.util.I18nUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * email util test
 *
 * @author xuxueli 2017-12-22 17:16:23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class I18nUtilTest {

    @Test
    public void test(){
        System.out.println(I18nUtil.getString("admin_name"));
        System.out.println(I18nUtil.getMultString("admin_name", "admin_name_full"));
        System.out.println(I18nUtil.getMultString());
    }

}
