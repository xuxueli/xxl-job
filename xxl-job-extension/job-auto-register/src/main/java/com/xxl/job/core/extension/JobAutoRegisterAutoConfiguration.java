package com.xxl.job.core.extension;

import com.xxl.job.core.extension.client.TaskContext;
import com.xxl.job.core.extension.server.RegisterServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lesl
 */
@Configuration
@Import({RegisterServer.class, TaskContext.class})
public class JobAutoRegisterAutoConfiguration {

}
