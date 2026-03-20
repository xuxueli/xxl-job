package com.xxl.job.writing.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 环境配置测试
 */
class EnvironmentConfigTest {

    /**
     * 开发环境配置测试
     */
    @SpringBootTest(properties = {
        "spring.profiles.active=dev",
        "writing.platform.jwt.secret=test-jwt-secret-key-for-testing-only-32-chars"
    })
    @Nested
    class DevEnvironmentTest {

        @Autowired
        private Environment environment;

        @Test
        void testDevEnvironmentConfig() {
            // 验证开发环境配置
            String mockEnabled = environment.getProperty("writing.platform.auth.mock-enabled");
            String strictValidation = environment.getProperty("writing.platform.jwt.strict-validation");
            String swaggerEnabled = environment.getProperty("springdoc.swagger-ui.enabled");

            assertNotNull(mockEnabled, "Mock enabled property should be configured");
            assertEquals("true", mockEnabled, "Mock authentication should be enabled in dev environment");
            assertEquals("false", strictValidation, "Strict validation should be disabled in dev environment");
            assertEquals("true", swaggerEnabled, "Swagger should be enabled in dev environment");
        }
    }

    /**
     * 生产环境配置测试
     */
    @SpringBootTest(properties = {
        "spring.profiles.active=prod",
        "writing.platform.jwt.secret=test-jwt-secret-key-for-testing-only-32-chars"
    })
    @Nested
    class ProdEnvironmentTest {

        @Autowired
        private Environment environment;

        @Test
        void testProdEnvironmentConfig() {
            // 验证生产环境配置
            String mockEnabled = environment.getProperty("writing.platform.auth.mock-enabled");
            String strictValidation = environment.getProperty("writing.platform.jwt.strict-validation");
            String swaggerEnabled = environment.getProperty("springdoc.swagger-ui.enabled");

            assertNotNull(mockEnabled, "Mock enabled property should be configured");
            assertEquals("false", mockEnabled, "Mock authentication should be disabled in prod environment");
            assertEquals("true", strictValidation, "Strict validation should be enabled in prod environment");
            assertEquals("false", swaggerEnabled, "Swagger should be disabled in prod environment");
        }
    }

    /**
     * 开发+本地环境配置测试
     */
    @SpringBootTest(properties = {
        "spring.profiles.active=dev,local",
        "writing.platform.jwt.secret=test-jwt-secret-key-for-testing-only-32-chars"
    })
    @Nested
    class DevLocalEnvironmentTest {

        @Autowired
        private Environment environment;

        @Test
        void testDevLocalEnvironmentConfig() {
            // 验证开发+本地环境配置继承
            String mockEnabled = environment.getProperty("writing.platform.auth.mock-enabled");
            String strictValidation = environment.getProperty("writing.platform.jwt.strict-validation");

            assertNotNull(mockEnabled, "Mock enabled property should be configured");
            assertEquals("true", mockEnabled, "Mock authentication should be enabled in dev+local environment");
            assertEquals("false", strictValidation, "Strict validation should be disabled in dev+local environment");
        }
    }
}