# xxl-job-executor-sample-nutzboot

用NutzBoot作为xxl-job-executor的示例

## 文件介绍

* MainLauncher.java NutzBoot启动类, 其中的init方法,扫描/加载/注册ioc容器内的IJobHandler
* XxlJobConfig.java 读取配置信息,声明XxlJobExecutor对象
* ShardingJobHandler.java和DemoJobHandler.java 2个示例IJobHandler实现类

本例子中添加的jetty和nutz mvc不是必须的,之所以添加只是因为其他sample都添加了一个简单的首页. 

纯跑xxl-job-executor的话,不需要加jetty和nutz mvc的starter.

## 环境要求

* JDK8u112 以上

## 我有疑问?

请访问 https://nutz.cn 获取帮助