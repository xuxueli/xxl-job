

## 问题
XXL-JOB 跟springboot结合使用的时候，默认会开启额外的端口9999。
此端口在生产环境中会造成问题。

此修改，避免新开端口，并复用spring boot自带的端口。
## 实现方式
借助Actuator的Endpoint的实现，对外开放执行器的相关接口。
## 使用方式
具体参考项目 xxl-job-executor-sample-springboot-no-extra-port
```
@SpringBootApplication
@Import({XxlJobAutoConfiguration.class})
public class XxlJobExecutorApplication 
```
application.properties文件中新增

- `management.endpoints.web.exposure.include=xxl-job`
用于开放新注册的endpoint

- `xxl.job.executor.address=http://{server_ip}:${server.port}/actuator/xxl-job`
可保持不变
- `xxl.job.executor.integratedSpringBoot=true`
用于指明使用spring boot的端口。

- `
xxl.job.executor.ip,xxl.job.executor.port`
两个属性，在xxl.job.executor.integratedSpringBoot=true的情况下，无用。
