package com.xxl.job.admin.common.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 字段验证配置 快速失败返回模式
 *
 * @author Rong.Jia
 * @date 2021/12/20
 */
@Configuration
public class ValidConfig {

    @Bean
    public Validator validator() {

        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

//    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {

        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();

        // 设置validator模式为快速失败返回
        postProcessor.setValidator(validator());
        return postProcessor;
    }
}
