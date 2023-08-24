package com.xxl.job.admin.common.config;

import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.xxl.job.admin.common.config.escape.StringTrimDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.util.CharsetUtil.UTF_8;

/**
 * 自定义消息转换器
 *
 * @author Rong.Jia
 * @date 2021/04/29 08:58
 */
@Configuration
@ConditionalOnClass(JSON.class)
public class HttpMessageConverterConfig {

    /**
     * http消息转换器。
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(CharsetUtil.charset(UTF_8));
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {

        // 1.定义一个converters转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        // 2.添加fastjson的配置信息，比如: 是否需要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        //Long类型转String类型
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        // ToStringSerializer 是这个包 com.alibaba.fastjson.serializer.ToStringSerializer
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setSerializerFeatures(SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteEnumUsingToString,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect
        );

        fastJsonConfig.setFeatures(Feature.TrimStringFieldValue, Feature.UseNativeJavaObject);
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.putDeserializer(String.class, new StringTrimDeserializer());
//        parserConfig.putDeserializer(Object.class, new StringTrimDeserializer());

//        fastJsonConfig.setParserConfig(parserConfig);

        // 3.在converter中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        fastMediaTypes.add(MediaType.APPLICATION_PDF);
        fastMediaTypes.add(MediaType.APPLICATION_XML);
        fastMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        fastMediaTypes.add(MediaType.MULTIPART_MIXED);
        fastMediaTypes.add(MediaType.TEXT_HTML);
        fastMediaTypes.add(MediaType.TEXT_XML);
        fastMediaTypes.add(MediaType.IMAGE_GIF);
        fastMediaTypes.add(MediaType.IMAGE_JPEG);
        fastMediaTypes.add(MediaType.IMAGE_PNG);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        // 4.返回HttpMessageConverters对象
        return fastConverter;
    }



}
