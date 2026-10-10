package com.xxl.job.admin.business.controller;

import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobListTemplateTest {

    @Test
    void parsesJobListTemplateWithTimezoneDropdowns() throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_34);
        configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");

        assertNotNull(configuration.getTemplate("business/job.list.ftl"));
    }
}
