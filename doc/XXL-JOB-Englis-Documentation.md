# 《A lightweight distributed task scheduling framework. "XXL-JOB"》

[![Build Status](https://travis-ci.org/xuxueli/xxl-job.svg?branch=master)](https://travis-ci.org/xuxueli/xxl-job)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-job.svg)](https://github.com/xuxueli/xxl-job/releases)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![Gitter](https://badges.gitter.im/xuxueli/xxl-job.svg)](https://gitter.im/xuxueli/xxl-job?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## 1. Brief introduction

### 1.1 Overview
XXL-JOB is a lightweight distributed task scheduling framework, the core design goal is to develop quickly, learning simple, lightweight, easy to expand. Is now open source and access to a number of companies online product line, download and use it now.
### 1.2 Features
- 1.Simple: support through the Web page on the task CRUD operation, simple operation, a minute to get started;
- 2.Dynamic: support dynamic modification of task status, pause / resume tasks, and termination of running tasks,immediate effect;
- 3.Dispatch center HA (center type): Dispatch with central design, "dispatch center" based on the cluster of Quartz implementation, can guarantee the scheduling - center HA;
- 4.Executer HA (Distributed): Task Distributed Execution, Task " Executer " supports cluster deployment to ensure that tasks perform HA;
- 5.Task Failover: Deploy the Excuter cluster,tasks will be smooth to switch excuter when the strategy of the router choose ‘failover’;
- 6.Consistency: "Dispatch Center" through the DB lock to ensure the consistency of cluster distributed scheduling,one task excuted for once;
- 7.Custom task parameters: support online configuration scheduling tasks into the parameters, immediate effect;
- 8.Scheduling thread pool: scheduling system multi-threaded trigger scheduling operation, to ensure accurate scheduling, not blocked;
- 9.Elastic expansion capacity: once the new executor machine on the line or off the assembly line, the next time scheduling will be re-assigned tasks;
- 10.Mail alarm: the task fails to support e-mail alarm, support configuring multiple email addresses to send bulk alert messages;
- 11.Status monitoring: support real-time monitoring of the progress of the task;
- 12.Rolling execution log: support online view scheduling results, and support Rolling real-time view of the executer output of the complete implementation of the log;
- 13.GLUE: provide Web IDE, support online development task logic code, dynamic release, real-time compiler effective, omit the deployment of the on-line process. Supports historical versions of 30 versions back;
- 14.Data Encryption: The communication between the dispatching center and the executor is used for data encryption, Enhancing the security of dispatching information;
- 15.Task Dependency: Support configuration subtask dependencies, When the parent task executed end and after the success of the implementation will take the initiative to trigger a second task execution, multiple sub tasks are separated by commas;
- 16.Push the Maven central warehouse: The latest stable version will be sent to the Maven central warehouse to facilitate user access and use;
- 17.Task registration: The executor automatically registers tasks periodically, and the dispatch center automatically finds the registered tasks and triggers execution. It also supports manual input of executor address;
- 18.Router strategy: A rich routing strategy is provided when the executor cluster is deployed, these include: first, last, poll, random, consistent HASH, least frequently used, least recently used, failover, busy over, sharding broadcast,etc.;
- 19.Report monitor: Support real-time view of running data, such as the number of tasks, the number of dispatch, the number of executors, etc .; and scheduling reports, such as scheduling date distribution, scheduling success map;
- 20.Script task: Support the development and operation of script tasks in GLUE mode, including shell, Python and other types of script;
- 21.Blocking handling strategy: The scheduling is too dense and the executor is too late to handle. The strategy includes: single machine serial (default), discarding the following scheduling, and Override the previous scheduling;
- 22.Failure handling strategy:Handling strategy when scheduling fails, the strategy includes: failure alarm (default), failure retry;
- 23.Sharding broadcast task: When an executor cluster is deployed, task routing strategy select "sharding broadcast", a task schedule will broadcast all the actuators in the cluster to perform it once, you can develop sharding tasks based on sharding parameters;
- 24.Dynamic sharding: The sharding broadcast task is sharded by the executors to support the dynamic expansion of the executor cluster to dynamically increase the number of shardings and cooperate with the business handle; In the large amount of data operations can significantly improve the task processing capacity and speed.
###  1.3 Development
In 2015, I created the XXL-JOB project repository on github and submitted the first commit, followed by the system structure design, UI selection, interactive design ...
In 2015 - November, XXL-JOB finally RELEASE the first big version of V1.0, then I will be released to OSCHINA, XXL-JOB OSCHINA won the popular recommendation of @红薯, the same period reached OSCHINA's " Popular move "ranked first and git.oschina open source software monthly heat ranked first, especially thanks for @红薯, thank you for the attention and support.
In 2015 - December, I will XXL-JOB published to our internal knowledge base, and get internal colleagues recognized.
In 2016 - 01 months, my company started XXL-JOB internal access and custom work, in this thank Yuan and Yin two colleagues contribution, but also to thank the internal other attention and support colleagues.
In 2017-05-13, the link of "let the code run" in "[the 62nd source of open source China Genesis](https://www.oschina.net/event/2236961)" held in Shanghai,, I stepped on and made a speech about the XXL-JOB, five hundred spectators in the audience reacted enthusiastically ([pictorial review](https://www.oschina.net/question/2686220_2242120)).
#### My company have access to XXL-JOB, internal alias "Ferrari" (Ferrari based on XXL-JOB V1.1 version customization, new access application recommended to upgrade the latest version).
According to the latest statistics, from 2016-01-21 to 2017-07-07 period, the system has been scheduled about 600,000 times, outstanding performance. New access applications recommend the latest version, because after several major updates, the system's task model, UI interaction model and the underlying scheduling communication model has a greater optimization and upgrading, the core function more stable and efficient.
So far, XXL-JOB has access to a number of companies online product line, access to scenes such as electronic commerce, O2O business and large data operations, as of 2016-07-19, XXL-JOB has access to the company But not limited to:

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
    - 34、咪咕互动娱乐有限公司（中国移动）
    - 35、北京诺亦腾科技有限公司
    - 36、增长引擎(北京)信息技术有限公司
	- ……
	
Welcome everyone's attention and use, XXL-JOB will also embrace changes, sustainable development.

### 1.4 Download

#### Source repository address (The latest code will be released in the two git warehouse in the same time)

Source repository address | Release Download
--- | ---
[https://github.com/xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | [Download](https://github.com/xuxueli/xxl-job/releases)  
[http://git.oschina.net/xuxueli0323/xxl-job](http://git.oschina.net/xuxueli0323/xxl-job) | [Download](http://git.oschina.net/xuxueli0323/xxl-job/releases)

#### Center repository address (The latest Release version：1.8.1)
```
<!-- http://repo1.maven.org/maven2/com/xuxueli/xxl-job-core/ -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>1.8.1</version>
</dependency>
```

#### Blog address

- [oschina address](http://my.oschina.net/xuxueli/blog/690978)
- [cnblogs address](http://www.cnblogs.com/xuxueli/p/5021979.html)

#### Technical exchange group (technical exchange only)

- Group 6：399758605
- Group 5：138274130    （Group is full, please add group 6）
- Group 4：464762661    （Group is full, please add group 6）
- Group 3：242151780    （Group is full, please add group 6）
- Group 2：438249535    （Group is full, please add group 6）
- Group 1：367260654    （Group is full, please add group 6）

### 1.5 Environment
- JDK：1.7+
- Servlet/JSP Spec：3.1/2.3
- Tomcat：8.5.x/Jetty9.2.x
- Spring-boot：1.5.x/Spring4.x
- Mysql：5.6+
- Maven：3+