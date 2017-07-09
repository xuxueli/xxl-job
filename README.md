
# 分布式任务调度平台XXL-JOB

[![Build Status](https://travis-ci.org/xuxueli/xxl-job.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-job)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-job.svg)](https://github.com/xuxueli/xxl-job/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![Gitter](https://badges.gitter.im/xuxueli/xxl-job.svg)](https://gitter.im/xuxueli/xxl-job?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

XXL-JOB是一个轻量级分布式任务调度框架，其核心设计目标是开发迅速、学习简单、轻量级、易扩展。现已开放源代码并接入多家公司线上产品线，开箱即用。

![输入图片说明](https://raw.githubusercontent.com/xuxueli/xxl-job/master/doc/images/xxl-logo.jpg "在这里输入图片标题")

### 文档

- 官方文档：[XXL-JOB官方文档](https://github.com/xuxueli/xxl-job/blob/master/doc/XXL-JOB官方文档.md)

### 特性
- 1、简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手；
- 2、动态：支持动态修改任务状态、暂停/恢复任务，以及终止运行中任务，即时生效；
- 3、调度中心HA（中心式）：调度采用中心式设计，“调度中心”基于集群Quartz实现，可保证调度中心HA；
- 4、执行器HA（分布式）：任务分布式执行，任务"执行器"支持集群部署，可保证任务执行HA；
- 5、任务Failover：执行器集群部署时，任务路由策略选择"故障转移"情况下调度失败时将会平滑切换执行器进行Failover；
- 6、一致性：“调度中心”通过DB锁保证集群分布式调度的一致性, 一次任务调度只会触发一次执行；
- 7、自定义任务参数：支持在线配置调度任务入参，即时生效；
- 8、调度线程池：调度系统多线程触发调度运行，确保调度精确执行，不被堵塞；
- 9、弹性扩容缩容：一旦有新执行器机器上线或者下线，下次调度时将会重新分配任务；
- 10、邮件报警：任务失败时支持邮件报警，支持配置多邮件地址群发报警邮件；
- 11、状态监控：支持实时监控任务进度；
- 12、Rolling执行日志：支持在线查看调度结果，并且支持以Rolling方式实时查看执行器输出的完整的执行日志；
- 13、GLUE：提供Web IDE，支持在线开发任务逻辑代码，动态发布，实时编译生效，省略部署上线的过程。支持30个版本的历史版本回溯。
- 14、数据加密：调度中心和执行器之间的通讯进行数据加密，提升调度信息安全性；
- 15、任务依赖：支持配置子任务依赖，当父任务执行结束且执行成功后将会主动触发一次子任务的执行, 多个子任务用逗号分隔；
- 16、推送maven中央仓库: 将会把最新稳定版推送到maven中央仓库, 方便用户接入和使用;
- 17、任务注册: 执行器会周期性自动注册任务, 调度中心将会自动发现注册的任务并触发执行。同时，也支持手动录入执行器地址；
- 18、路由策略：执行器集群部署时提供丰富的路由策略，包括：第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移；
- 19、运行报表：支持实时查看运行数据，如任务数量、调度次数、执行器数量等；以及调度报表，如调度日期分布图，调度成功分布图等；
- 20、脚本任务：支持以GLUE模式开发和运行脚本任务，包括Shell、Python等类型脚本;
- 21、阻塞处理策略：调度过于密集执行器来不及处理时的处理策略，策略包括：单机串行（默认）、丢弃后续调度、覆盖之前调度；
- 22、失败处理策略；调度失败时的处理策略，策略包括：失败告警（默认）、失败重试；

### 架构图

![输入图片说明](https://static.oschina.net/uploads/img/201705/11225449_rKMi.png "在这里输入图片标题")

### 发展
于2015年中，我在github上创建XXL-JOB项目仓库并提交第一个commit，随之进行系统结构设计，UI选型，交互设计……

于2015-11月，XXL-JOB终于RELEASE了第一个大版本V1.0， 随后我将之发布到OSCHINA，XXL-JOB在OSCHINA上获得了@红薯的热门推荐，同期分别达到了OSCHINA的“热门动弹”排行第一和git.oschina的开源软件月热度排行第一，在此特别感谢红薯，感谢大家的关注和支持。

于2015-12月，我将XXL-JOB发表到我司内部知识库，并且得到内部同事认可。

于2016-01月，我司展开XXL-JOB的内部接入和定制工作，在此感谢袁某和尹某两位同事的贡献，同时也感谢内部其他给与关注与支持的同事。

于2017-05-13，在上海举办的 "[第62期开源中国源创会](https://www.oschina.net/event/2236961)" 的 "放码过来" 环节，我登台对XXL-JOB做了演讲，台下五百位在场观众反响热烈（[图文回顾](https://www.oschina.net/question/2686220_2242120) ）。

#### 我司大众点评目前已接入XXL-JOB，内部别名《Ferrari》（Ferrari基于XXL-JOB的V1.1版本定制而成，新接入应用推荐升级最新版本）。
据最新统计, 自2016-01-21接入至2017-07-07期间，该系统已调度约60万余次，表现优异。新接入应用推荐使用最新版本，因为经过数个大版本的更新，系统的任务模型、UI交互模型以及底层调度通讯模型都有了较大的优化和提升，核心功能更加稳定高效。

至今，XXL-JOB已接入多家公司的线上产品线，接入场景如电商业务，O2O业务和大数据作业等，截止2016-07-19为止，XXL-JOB已接入的公司包括不限于：
    
	- 1、大众点评；
	- 2、山东学而网络科技有限公司；
	- 3、安徽慧通互联科技有限公司；
	- 4、人人聚财金服；
	- 5、上海棠棣信息科技股份有限公司
	- 6、运满满
	- 7、米其林 (中国区)
	- 8、妈妈联盟
	- 9、九樱天下（北京）信息技术有限公司
	- 10、万普拉斯科技有限公司(一加手机)
	- 11、上海亿保健康管理有限公司
	- 12、海尔馨厨 (海尔)
	- 13、河南大红包电子商务有限公司
	- 14、成都顺点科技有限公司
	- 15、深圳市怡亚通
	- 16、深圳麦亚信科技股份有限公司
	- 17、上海博莹科技信息技术有限公司
	- 18、中国平安科技有限公司
	- 19、杭州知时信息科技有限公司
	- 20、博莹科技（上海）有限公司
	- 21、成都依能股份有限责任公司
	- 22、湖南高阳通联信息技术有限公司
	- 23、深圳市邦德文化发展有限公司
	- 24、福建阿思可网络教育有限公司
	- 25、优信二手车
	- 26、上海悠游堂投资发展股份有限公司
	- 27、北京粉笔蓝天科技有限公司
	- 28、中秀科技(无锡)有限公司
	- 29、武汉空心科技有限公司
	- 30、北京蚂蚁风暴科技有限公司
	- 31、四川互宜达科技有限公司
    - 32、钱包行云（北京）科技有限公司
    - 33、重庆欣才集团
	- ……

欢迎大家的关注和使用，XXL-JOB也将拥抱变化，持续发展。


### 下载

#### 源码仓库地址 (将会在两个git仓库同步发布最新代码)

源码仓库地址 | Release Download
--- | ---
[https://github.com/xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | [Download](https://github.com/xuxueli/xxl-job/releases)  
[http://git.oschina.net/xuxueli0323/xxl-job](http://git.oschina.net/xuxueli0323/xxl-job) | [Download](http://git.oschina.net/xuxueli0323/xxl-job/releases)


#### 中央仓库地址 (最新Release版本：1.7.2)

```
<!-- http://repo1.maven.org/maven2/com/xuxueli/xxl-job-core/ -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>1.7.2</version>
</dependency>
```

#### 博客地址 (将会在两个博客同步更新文档)

- [oschina地址](http://my.oschina.net/xuxueli/blog/690978)
- [cnblogs地址](http://www.cnblogs.com/xuxueli/p/5021979.html)

#### 技术交流群 (仅作技术交流)

- 群4：464762661    [![image](http://pub.idqqimg.com/wpa/images/group.png)](http://shang.qq.com/wpa/qunwpa?idkey=c1660fbf8f81934b6f9095f9212f413ed2b127e72223502bb3c65888a0236ad3 )
- 群3：242151780    （群即将满，请加群4）
- 群2：438249535    （群即将满，请加群4）
- 群1：367260654    （群即将满，请加群4）


### 报告问题
XXL-JOB托管在Github上，如有问题可在 [ISSUES](https://github.com/xuxueli/xxl-job/issues/) 上提问，也可以加入上文技术交流群；

### 接入登记（登记仅为了推广，产品开源免费）
更多接入公司，欢迎在github [登记](https://github.com/xuxueli/xxl-job/issues/1 )

### 开源协议
产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。

XXL-JOB采用GPLv3协议，目的是为了保证用户的自由使用权利。协议可避免专利申请的特殊危险 "the GPL assures that patents cannot be used to render the program non-free.（摘自GPLv3）"。  

---
### 支持的话可以扫一扫，支持 [XXL系列](https://github.com/xuxueli) 的建设：）

微信：![输入图片说明](https://static.oschina.net/uploads/img/201707/07214300_qhxT.png "在这里输入图片标题")
支付宝：![输入图片说明](http://images2015.cnblogs.com/blog/554415/201605/554415-20160513183306234-1939652116.png "在这里输入图片标题")
