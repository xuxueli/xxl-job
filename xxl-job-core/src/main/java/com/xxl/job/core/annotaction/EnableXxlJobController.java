package com.xxl.job.core.annotaction;

import com.xxl.job.core.controller.XxlJobHandlerController;
import com.xxl.job.core.properties.XxlJobProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 导入Xxljb 的springboot controller接口
 *
 * @author SongLongKuan
 * @time 2021/2/22 9:55 上午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {
        XxlJobProperties.class,
        XxlJobHandlerController.class
})
public @interface EnableXxlJobController {
}
