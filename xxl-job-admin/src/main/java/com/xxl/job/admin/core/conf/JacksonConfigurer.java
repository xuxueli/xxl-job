package com.xxl.job.admin.core.conf;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Json配置类，Long和BigDecimal过时，前端js会出现丢失精度问题
 * 
 * @author 单红宇
 *
 */
@Configuration
public class JacksonConfigurer {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return builder -> {
			builder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
			builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
		};
	}
}
