package com.xxl.job.admin.business.model;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XxlJobGroupTest {

    @Test
    public void accessTokenIsNotExposedInJsonOrToString() throws Exception {
        XxlJobGroup group = new XxlJobGroup();
        group.setId(1);
        group.setAppname("executor-one");
        group.setAccessToken("secret-token");

        String json = new ObjectMapper().writeValueAsString(group);

        assertTrue(json.contains("executor-one"));
        assertFalse(json.contains("secret-token"));
        assertFalse(group.toString().contains("secret-token"));
    }
}
