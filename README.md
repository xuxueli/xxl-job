<p align="center" >
    <img src="https://www.xuxueli.com/doc/static/xxl-job/images/xxl-logo.jpg" width="150">
    <h3 align="center">XXL-JOB</h3>
    <p align="center">
        XXL-JOB, a distributed task scheduling framework.
        <br>
        <a href="https://www.xuxueli.com/xxl-job/"><strong>-- Home Page --</strong></a>
        <br>
        <br>
        <a href="https://github.com/xuxueli/xxl-job/actions">
            <img src="https://github.com/xuxueli/xxl-job/workflows/Java%20CI/badge.svg" >
        </a>
        <a href="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/">
            <img src="https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/badge.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-job/releases">
         <img src="https://img.shields.io/github/release/xuxueli/xxl-job.svg" >
        </a>
        <a href="https://github.com/xuxueli/xxl-job/">
            <img src="https://img.shields.io/github/stars/xuxueli/xxl-job" >
        </a>
        <a href="https://hub.docker.com/r/xuxueli/xxl-job-admin/">
            <img src="https://img.shields.io/docker/pulls/xuxueli/xxl-job-admin" >
        </a>
        <a href="http://www.gnu.org/licenses/gpl-3.0.html">
         <img src="https://img.shields.io/badge/license-GPLv3-blue.svg" >
        </a>
        <a href="https://www.xuxueli.com/page/donate.html">
           <img src="https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat" >
        </a>
    </p>
</p>

## 介绍

本项目是[xxl-job](https://github.com/xuxueli/xxl-job) 项目的克隆项目，目的在于优化和修复原项目中的问题。原项目中最新的版本号是2.4.0-SNAPSHOT，本项目从2.5.0版本号开始

## 项目说明
|服务|描述|
|---|---|
|xxl-job-admin|管理端，将此服务部署到服务器或者本地运行， 允许运行多个|
|xxl-job-core|项目的公共包，核心配置|
|xxl-job-executor|执行器|
|xxl-job-executor-samples|执行器使用示例|

## 配置说明
### xxl-job-executor配置
在application.yml，或者application.properties增加如下配置
```yaml
xxl:
  job:  
  enabled: true # 必须为true，否则不生效        
  admin:
    addresses: localhost:8080 # 调度中心部署跟地址 [必填]，允许多个
    username: admin # 管理端账号，默认：admin
    password: 123456 # 管理端密码，默认：123456
  executor:
    app-name: sms-service # 执行器AppName[必填]:执行器心跳注册分组依据
    host: 10.30.123.132 # 执行器IP[选填]:默认为空表示自动获取IP,多网卡时可手动设置指定IP,该IP不会绑定Host仅作为通讯实用.地址信息用于'执行器注册'和'调度中心请求并触发任务'.
    port: 8080 # 执行器端口号[选填],单机部署多个执行器时,注意要配置不同执行器端口.
    log-path: logs/etl-job/job-handler # 执行器运行日志文件存储磁盘路径[选填]:需要对该路径拥有读写权限,为空则使用默认路径. 默认：执行器运行日志文件存储磁盘路径[选填]:需要对该路径拥有读写权限,为空则使用默认路径.
    logRetentionDays: 7 # 执行器日志文件保存天数[选填],过期日志自动清理,限制值大于等于3时生效;否则,如-1,关闭自动清理功能. 默认: 7
```

## 使用说明
### 通过JobHelper.getJobParam()
```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobTest {

    @Job(value = "JobTest")  // value: JobHandler内容
    public void jobTest() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("---------xxlJobTest定时任务执行成功-------- {}", jobParam);
    }
}

```
### 通过方法参数传递
此方法需要传递的参数，必须在是参数列表的第一个
```java
@Slf4j
@Component
public class JobTest {

    @Job(value = "JobTest") // value: JobHandler内容
    public void jobTest(JobInfoDTO data) {
        log.info("---------xxlJobTest定时任务执行成功 参数传递-------- {}", data);
    }
}
```

|版本号|解决的问题|
|---|---|
|[2.5.0](https://github.com/a852203465/xxl-job/releases/tag/2.5.0)|`重构项目`，并移除xxl-job-core中的netty server，使用spring mvc替代netty server的功能，重用客户端spring boot端口号，不再额外开启9999端口号|