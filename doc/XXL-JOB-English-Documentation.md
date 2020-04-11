## 《Distributed task scheduling framework XXL-JOB》

[![Actions Status](https://github.com/xuxueli/xxl-job/workflows/Java%20CI/badge.svg)](https://github.com/xuxueli/xxl-job/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xuxueli/xxl-job/)
[![GitHub release](https://img.shields.io/github/release/xuxueli/xxl-job.svg)](https://github.com/xuxueli/xxl-job/releases)
[![GitHub stars](https://img.shields.io/github/stars/xuxueli/xxl-job)](https://github.com/xuxueli/xxl-job/)
[![Docker Status](https://img.shields.io/docker/pulls/xuxueli/xxl-job-admin)](https://hub.docker.com/r/xuxueli/xxl-job-admin/)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![donate](https://img.shields.io/badge/%24-donate-ff69b4.svg?style=flat)](https://www.xuxueli.com/page/donate.html)

[TOCM]

[TOC]

## 1. Brief introduction

### 1.1 Overview
XXL-JOB is a distributed task scheduling framework, the core design goal is to develop quickly, learning simple, lightweight, easy to expand. Is now open source and access to a number of companies online product line, download and use it now.

> English document update slightly delayed, Please check the Chinese version for the latest document.

### 1.2 Features
- 1.Simple: support through the Web page on the task CRUD operation, simple operation, a minute to get started;
- 2.Dynamic: support dynamic modification of task status, pause / resume tasks, and termination of running tasks,immediate effect;
- 3.Dispatch center HA (center type): Dispatch with central design, "dispatch center" based on the cluster of Quartz implementation, can guarantee the scheduling - center HA;
- 4.Executor HA (Distributed): Task Distributed Execution, Task " Executer " supports cluster deployment to ensure that tasks perform HA;
- 5.Task Failover: Deploy the Excutor cluster,tasks will be smooth to switch excuter when the strategy of the router choose ‘failover’;
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
- 25、Event trigger：In addition to "Cron" and "Task Dependency" to trigger tasks, support event-based triggering tasks. The dispatch center provides API service that triggers a single execution of the task, it can be triggered flexibly according to business events. 


###  1.3 Development
In 2015, I created the XXL-JOB project repository on github and submitted the first commit, followed by the system structure design, UI selection, interactive design ...
In 2015 - November, XXL-JOB finally RELEASE the first big version of V1.0, then I will be released to OSCHINA, XXL-JOB OSCHINA won the popular recommendation of @红薯, the same period reached OSCHINA's " Popular move "ranked first and git.oschina open source software monthly heat ranked first, especially thanks for @红薯, thank you for the attention and support.
In 2015 - December, I will XXL-JOB published to our internal knowledge base, and get internal colleagues recognized.
In 2016 - 01 months, my company started XXL-JOB internal access and custom work, in this thank Yuan and Yin two colleagues contribution, but also to thank the internal other attention and support colleagues.
In 2017-05-13, the link of "let the code run" in "[the 62nd source of open source China Genesis](https://www.oschina.net/event/2236961)" held in Shanghai,, I stepped on and made a speech about the XXL-JOB, five hundred spectators in the audience reacted enthusiastically ([pictorial review](https://www.oschina.net/question/2686220_2242120)).
> Our company have access to XXL-JOB, internal alias "Ferrari" (Ferrari based on XXL-JOB V1.1 version customization, new access application recommended to upgrade the latest version).
According to the latest statistics, from 2016-01-21 to 2017-07-07 period, the system has been scheduled about 600,000 times, outstanding performance. New access applications recommend the latest version, because after several major updates, the system's task model, UI interaction model and the underlying scheduling communication model has a greater optimization and upgrading, the core function more stable and efficient.

So far, XXL-JOB has access to a number of companies online product line, access to scenes such as electronic commerce, O2O business and large data operations, as of 2016-07-19, XXL-JOB has access to the company But not limited to:

	- 1、大众点评【美团点评】
	- 2、山东学而网络科技有限公司；
	- 3、安徽慧通互联科技有限公司；
	- 4、人人聚财金服；
	- 5、上海棠棣信息科技股份有限公司
	- 6、运满满【运满满】
	- 7、米其林 (中国区)【米其林】
	- 8、妈妈联盟
	- 9、九樱天下（北京）信息技术有限公司
	- 10、万普拉斯科技有限公司【一加手机】
	- 11、上海亿保健康管理有限公司
	- 12、海尔馨厨【海尔】
	- 13、河南大红包电子商务有限公司
	- 14、成都顺点科技有限公司
	- 15、深圳市怡亚通
	- 16、深圳麦亚信科技股份有限公司
	- 17、上海博莹科技信息技术有限公司
	- 18、中国平安科技有限公司【中国平安】
	- 19、杭州知时信息科技有限公司
	- 20、博莹科技（上海）有限公司
	- 21、成都依能股份有限责任公司
	- 22、湖南高阳通联信息技术有限公司
	- 23、深圳市邦德文化发展有限公司
	- 24、福建阿思可网络教育有限公司
	- 25、优信二手车【优信】
	- 26、上海悠游堂投资发展股份有限公司【悠游堂】
	- 27、北京粉笔蓝天科技有限公司
	- 28、中秀科技(无锡)有限公司
	- 29、武汉空心科技有限公司
	- 30、北京蚂蚁风暴科技有限公司
	- 31、四川互宜达科技有限公司
	- 32、钱包行云（北京）科技有限公司
	- 33、重庆欣才集团
    - 34、咪咕互动娱乐有限公司【中国移动】
    - 35、北京诺亦腾科技有限公司
    - 36、增长引擎(北京)信息技术有限公司
    - 37、北京英贝思科技有限公司
    - 38、刚泰集团
    - 39、深圳泰久信息系统股份有限公司
    - 40、随行付支付有限公司
    - 41、广州瀚农网络科技有限公司
    - 42、享点科技有限公司
    - 43、杭州比智科技有限公司
    - 44、圳临界线网络科技有限公司
    - 45、广州知识圈网络科技有限公司
    - 46、国誉商业上海有限公司
    - 47、海尔消费金融有限公司，嗨付、够花【海尔】
    - 48、广州巴图鲁信息科技有限公司
    - 49、深圳市鹏海运电子数据交换有限公司
    - 50、深圳市亚飞电子商务有限公司
    - 51、上海趣医网络有限公司
    - 52、聚金资本
    - 53、北京父母邦网络科技有限公司
    - 54、中山元赫软件科技有限公司
    - 55、中商惠民(北京)电子商务有限公司
    - 56、凯京集团
    - 57、华夏票联（北京）科技有限公司
    - 58、拍拍贷【拍拍贷】
    - 59、北京尚德机构在线教育有限公司
    - 60、任子行股份有限公司
    - 61、北京时态电子商务有限公司
    - 62、深圳卷皮网络科技有限公司
    - 63、北京安博通科技股份有限公司
    - 64、未来无线网
    - 65、厦门瓷禧网络有限公司
    - 66、北京递蓝科软件股份有限公司
    - 67、郑州创海软件科技公司
    - 68、北京国槐信息科技有限公司
    - 69、浪潮软件集团
    - 70、多立恒(北京)信息技术有限公司
    - 71、广州极迅客信息科技有限公司
    - 72、赫基（中国）集团股份有限公司
    - 73、海投汇
    - 74、上海润益创业孵化器管理股份有限公司
    - 75、汉纳森（厦门）数据股份有限公司
    - 76、安信信托
    - 77、岚儒财富
    - 78、捷道软件
    - 79、湖北享七网络科技有限公司
    - 80、湖南创发科技责任有限公司
    - 81、深圳小安时代互联网金融服务有限公司
    - 82、湖北享七网络科技有限公司
    - 83、钱包行云(北京)科技有限公司
    - 84、360金融【360】
    - 85、易企秀
    - 86、摩贝（上海）生物科技有限公司
    - 87、广东芯智慧科技有限公司
    - 88、联想集团【联想】
    - 89、怪兽充电
    - 90、行圆汽车
    - 91、深圳店店通科技邮箱公司
    - 92、京东【京东】
    - 93、米庄理财
    - 94、咖啡易融
    - 95、梧桐诚选
    - 96、恒大地产【恒大】
    - 97、昆明龙慧
    - 98、上海涩瑶软件
    - 99、易信【网易】
    - 100、铜板街
    - 101、杭州云若网络科技有限公司
    - 102、特百惠（中国）有限公司
    - 103、常山众卡运力供应链管理有限公司
    - 104、深圳立创电子商务有限公司
    - 105、杭州智诺科技股份有限公司
    - 106、北京云漾信息科技有限公司
    - 107、深圳市多银科技有限公司
    - 108、亲宝宝
    - 109、上海博卡软件科技有限公司
    - 110、智慧树在线教育平台
    - 111、米族金融
    - 112、北京辰森世纪
    - 113、云南滇医通
    - 114、广州市分领网络科技有限责任公司
    - 115、浙江微能科技有限公司
    - 116、上海馨飞电子商务有限公司
    - 117、上海宝尊电子商务有限公司
    - 118、直客通科技技术有限公司
    - 119、科度科技有限公司
    - 120、上海数慧系统技术有限公司
    - 121、我的医药网
    - 122、多粉平台
    - 123、铁甲二手机
    - 124、上海海新得数据技术有限公司
    - 125、深圳市珍爱网信息技术有限公司【珍爱网】
    - 126、小蜜蜂
    - 127、吉荣数科技
    - 128、上海恺域信息科技有限公司
    - 129、广州荔支网络有限公司【荔枝FM】
    - 130、杭州闪宝科技有限公司
    - 131、北京互联新网科技发展有限公司
    - 132、誉道科技
    - 133、山西兆盛房地产开发有限公司
    - 134、北京蓝睿通达科技有限公司
    - 135、月亮小屋（中国）有限公司【蓝月亮】
    - 136、青岛国瑞信息技术有限公司
    - 137、博雅云计算（北京）有限公司
    - 138、华泰证券香港子公司
    - 139、杭州东方通信软件技术有限公司
    - 140、武汉博晟安全技术股份有限公司
    - 141、深圳市六度人和科技有限公司
    - 142、杭州趣维科技有限公司（小影）
    - 143、宁波单车侠之家科技有限公司【单车侠】
    - 144、丁丁云康信息科技（北京）有限公司
    - 145、云钱袋
    - 146、南京中兴力维
    - 147、上海矽昌通信技术有限公司
    - 148、深圳萨科科技
    - 149、中通服创立科技有限责任公司
    - 150、深圳市对庄科技有限公司
    - 151、上证所信息网络有限公司
    - 152、杭州火烧云科技有限公司【婚礼纪】
    - 153、天津青芒果科技有限公司【芒果头条】
    - 154、长飞光纤光缆股份有限公司
    - 155、世纪凯歌（北京）医疗科技有限公司
    - 156、浙江霖梓控股有限公司
    - 157、江西腾飞网络技术有限公司
    - 158、安迅物流有限公司
    - 159、肉联网
    - 160、北京北广梯影广告传媒有限公司
    - 161、上海数慧系统技术有限公司
    - 162、大志天成
    - 163、上海云鹊医
    - 164、上海云鹊医
    - 165、墨迹天气【墨迹天气】
    - 166、上海逸橙信息科技有限公司
    - 167、沅朋物联
    - 168、杭州恒生云融网络科技有限公司
    - 169、绿米联创
    - 170、重庆易宠科技有限公司
    - 171、安徽引航科技有限公司（乐职网）
    - 172、上海数联医信企业发展有限公司
    - 173、良彬建材
    - 174、杭州求是同创网络科技有限公司
    - 175、荷马国际
    - 176、点雇网
    - 177、深圳市华星光电技术有限公司
    - 178、厦门神州鹰软件科技有限公司
    - 179、深圳市招商信诺人寿保险有限公司
    - 180、上海好屋网信息技术有限公司
    - 181、海信集团【海信】
    - 182、信凌可信息科技（上海）有限公司
    - 183、长春天成科技发展有限公司
    - 184、用友金融信息技术股份有限公司【用友】
    - 185、北京咖啡易融有限公司
    - 186、国投瑞银基金管理有限公司
    - 187、晋松(上海)网络信息技术有限公司
    - 188、深圳市随手科技有限公司【随手记】
    - 189、深圳水务科技有限公司
    - 190、易企秀【易企秀】
    - 191、北京磁云科技
    - 192、南京蜂泰互联网科技有限公司
    - 193、章鱼直播
    - 194、奖多多科技
    - 195、天津市神州商龙科技股份有限公司
    - 196、岩心科技
    - 197、车码科技（北京）有限公司
    - 198、贵阳市投资控股集团
    - 199、康旗股份
    - 200、龙腾出行
    - 201、杭州华量软件
    - 202、合肥顶岭医疗科技有限公司
    - 203、重庆表达式科技有限公司
    - 204、上海米道信息科技有限公司
    - 205、北京益友会科技有限公司
    - 206、北京融贯电子商务有限公司
    - 207、中国外汇交易中心
    - 208、中国外运股份有限公司
    - 209、中国上海晓圈教育科技有限公司
    - 210、普联软件股份有限公司
    - 211、北京科蓝软件股份有限公司
    - 212、江苏斯诺物联科技有限公司
    - 213、北京搜狐-狐友【搜狐】
    - 214、新大陆网商金融
    - 215、山东神码中税信息科技有限公司
    - 216、河南汇顺网络科技有限公司
    - 217、北京华夏思源科技发展有限公司
    - 218、上海东普信息科技有限公司
    - 219、上海鸣勃网络科技有限公司
    - 220、广东学苑教育发展有限公司
    - 221、深圳强时科技有限公司
    - 222、上海云砺信息科技有限公司
    - 223、重庆愉客行网络有限公司
    - 224、数云
    - 225、国家电网运检部
    - 226、杭州找趣
    - 227、浩鲸云计算科技股份有限公司
    - 228、科大讯飞【科大讯飞】
    - 229、杭州行装网络科技有限公司
    - 230、即有分期金融
    - 231、深圳法司德信息科技有限公司
    - 232、上海博复信息科技有限公司
    - 233、杭州云嘉云计算有限公司
    - 234、有家民宿(有家美宿)
    - 235、北京赢销通软件技术有限公司
    - 236、浙江聚有财金融服务外包有限公司
    - 237、易族智汇(北京)科技有限公司
    - 238、合肥顶岭医疗科技开发有限公司
    - 239、车船宝(深圳)旭珩科技有限公司)
    - 240、广州富力地产有限公司
    - 241、氢课（上海）教育科技有限公司
    - 242、武汉氪细胞网络技术有限公司
    - 243、杭州有云科技有限公司
    - 244、上海仙豆智能机器人有限公司
    - 245、拉卡拉支付股份有限公司【拉卡拉】
    - 246、虎彩印艺股份有限公司
    - 247、北京数微科技有限公司
    - 248、广东智瑞科技有限公司
    - 249、找钢网
    - 250、九机网
    - 251、杭州跑跑网络科技有限公司
    - 252、深圳未来云集
    - 253、杭州每日给力科技有限公司
    - 254、上海齐犇信息科技有限公司
    - 255、滴滴出行【滴滴】
    - 256、合肥云诊信息科技有限公司
    - 257、云知声智能科技股份有限公司
    - 258、南京坦道科技有限公司
    - 259、爱乐优（二手平台）
    - 260、猫眼电影（私有化部署）【猫眼电影】
    - 261、美团大象（私有化部署）【美团大象】
    - 262、作业帮教育科技（北京）有限公司【作业帮】
    - 263、北京小年糕互联网技术有限公司
    - 264、山东矩阵软件工程股份有限公司
    - 265、陕西国驿软件科技有限公司
    - 266、君开信息科技
    - 267、村鸟网络科技有限责任公司
    - 268、云南国际信托有限公司
    - 269、金智教育
    - 270、珠海市筑巢科技有限公司
    - 271、上海百胜软件股份有限公司
    - 272、深圳市科盾科技有限公司
    - 273、哈啰出行
    - 274、途虎养车
    - 275、卡思优派人力资源集团
    - 276、南京观为智慧软件科技有限公司
    - 277、杭州城市大脑科技有限公司
    - 278、猿辅导
	- ……

> The company that access and use this product is welcome to register at the [address](https://github.com/xuxueli/xxl-job/issues/1 ), only for product promotion. 

Welcome everyone's attention and use, XXL-JOB will also embrace changes, sustainable development.

### 1.4 Download

#### Documentation
- [中文文档](https://www.xuxueli.com/xxl-job/)
- [English Documentation](https://www.xuxueli.com/xxl-job/en/)

#### Source repository address (The latest code will be released in the two git warehouse in the same time)

Source repository address | Release Download
--- | ---
[https://github.com/xuxueli/xxl-job](https://github.com/xuxueli/xxl-job) | [Download](https://github.com/xuxueli/xxl-job/releases)  
[http://gitee.com/xuxueli0323/xxl-job](http://gitee.com/xuxueli0323/xxl-job) | [Download](http://gitee.com/xuxueli0323/xxl-job/releases)

#### Center repository address (The latest Release version：1.8.1)
```
<!-- http://repo1.maven.org/maven2/com/xuxueli/xxl-job-core/ -->
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>1.8.2</version>
</dependency>
```

#### Technical exchange group
- [社区交流](https://www.xuxueli.com/page/community.html)
- [Gitter](https://gitter.im/xuxueli/xxl-job)

### 1.5 Environment
- JDK：1.7+
- Servlet/JSP Spec：3.1/2.3
- Tomcat：8.5.x/Jetty9.2.x
- Spring-boot：1.5.x/Spring4.x
- Mysql：5.6+
- Maven：3+


## 2. Quick Start

### 2.1 Init database
Please download project source code，get db scripts and execute, it will generate 16 tables if succeed.

The relative path of db scripts is as follows:

    /xxl-job/doc/db/tables_xxl_job.sql

The xxl-job-admin can be deployed as a cluster,all nodes of the cluster must connect to the same mysql instance.

If mysql instances is deployed in master-slave mode,all nodes of the cluster must connect to master instace.

### 2.2 Compile
Source code is organized by maven,unzip it and structure is as follows:

    xxl-job-admin：schedule admin center
    xxl-job-core：public common dependent library
    xxl-job-executor：executor Sample(Select appropriate version of executor,Can be used directly,You can also refer to it and transform existing projects into executors）
        ：xxl-job-executor-sample-spring：Spring version，executors managed by Spring，general and recommend;
        ：xxl-job-executor-sample-springboot：Springboot version，executors managed by Springboot;
        ：xxl-job-executor-sample-jfinal：JFinal version，executors managed by JFinal;
	
### 2.3 Configure and delploy "Schedule Center"	

    schedule center project:xxl-job-admin
    target:Centralized management、Schedule and trigger task

#### Step 1:Configure Schedule Center
Configure file’s path of schedule center is as follows:

    /xxl-job/xxl-job-admin/src/main/resources/application.properties


The concrete contet describe as follows:

    ### JDBC connection info of schedule center：keep Consistent with chapter 2.1
    xxl.job.db.driverClass=com.mysql.jdbc.Driver
    xxl.job.db.url=jdbc:mysql://localhost:3306/xxl-job?useUnicode=true&characterEncoding=UTF-8
    xxl.job.db.user=root
    xxl.job.db.password=root_pwd
    
    ### Alarm mailbox
    xxl.job.mail.host=smtp.163.com
    xxl.job.mail.port=25
    xxl.job.mail.username=ovono802302@163.com
    xxl.job.mail.password=asdfzxcv
    xxl.job.mail.sendFrom=ovono802302@163.com
    xxl.job.mail.sendNick=《任务调度平台XXL-JOB》
    
    ### Login account
    xxl.job.login.username=admin
    xxl.job.login.password=123456
    
    ### TOKEN used for communication between the executor and schedule center, enabled if it’s not null
    xxl.job.accessToken=
    
    ### Internationalized Settings, the default is Chinese version，Switch to English when the value is "en".
    xxl.job.i18n=en

#### Step 2:Deploy:
If you has finished step 1,then you can compile the project in maven and deploy the war package to tomcat.
the url to visit is :http://localhost:8080/xxl-job-admin (this address will be used by executor and use it as callback url),the index page after login in is as follow

![index page after login in](https://www.xuxueli.com/doc/static/xxl-job/images/img_6yC0.png "index page after login in")

Now,the “xxl-job-admin” project is deployed success.

#### Step3:schedule center Cluster(Option):
xxl-job-admin can be deployed as a cluster to improve system availability.

Prerequisites for cluster is to keep all node configuration(db and login account info) consistent with each other. Different xxl-job-admin cluster distinguish with each other by db configuration.

xxl-job-admin can be visited through nginx proxy and configure a domain for nginx,and the domain url can be configured as the executor’s callback url.

### 2.4 Configur and Deploy "xxl-job-executor-example"

    Executor Project:xxl-job-executor-example (if you want to create new executor project you can refer this demo);
    Target:receive xxl-job-admin’s schedule command and execute it;
    
#### Step 1:import maven dependence
Pleast confirm import xxl-job-core jar in pom.xml;
    
#### Step 2:Executor Configuration
Relative path of the executor configuration file is as follows:

    /xxl-job/xxl-job-executor-samples/xxl-job-executor-sample-spring/src/main/resources/xxl-job-executor.properties

The concret content of configuration file as follows:

    ### xxl-job admin address list：xxl-job-admin address list: Multiple addresses are separated by commas,this address is used for "heart beat and register" and "task execution result callback" between the executor and xxl-job-admin.
    xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
    
    ### xxl.job.executor.appname is used to group by executors
    xxl.job.executor.appname=xxl-job-executor-sample
    ### xxl.job.executor.ip :1,used to register with xxl-job-admin;2,xxl-job-admin dispatch task to executor through it;3,if it is blank executor will get ip automatically, multi network card need to be configured.
    xxl.job.executor.ip=
    ### xxl.job.executor.port :the port of the executor runned by,if multiple executor instance run on the same computer the port must different with each other
    xxl.job.executor.port=9999
    
    ### xxl-job log path：runtime log path of the job instance
    xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler/
    
    ### xxl-job, access token：xxl-job access token,enabled if it not blank
    xxl.job.accessToken=


#### Step 3:executor configuration

configure file path of executor:

    /xxl-job/xxl-job-executor-samples/xxl-job-executor-sample-spring/src/main/resources/applicationcontext-xxl-job.xml

Concrete contet describe as follows：

```
<!-- configure 01、JobHandler scan path：auto scan JobHandler bean managed by container -->
<context:component-scan base-package="com.xxl.job.executor.service.jobhandler" />

<!-- configure 02、Excutor：executer core configure -->
<bean id="xxlJobExecutor" class="com.xxl.job.core.executor.XxlJobExecutor" init-method="start" destroy-method="destroy" >
    <!-- executor IP[required]，auto get if it blank -->
    <property name="ip" value="${xxl.job.executor.ip}" />
    <!-- executor port[required] -->
    <property name="port" value="${xxl.job.executor.port}" />
    <!-- executor AppName[required]，auto register will be closed if it blank -->
    <property name="appname" value="${xxl.job.executor.appname}" />
    <!-- register center address of executor [required]，auto register will be closed if it blank -->
    <property name="adminAddresses" value="${xxl.job.admin.addresses}" />
    <!-- log path of executor[required] -->
    <property name="logPath" value="${xxl.job.executor.logpath}" />
    <!-- access token, match check enabled if it not blank[required] -->
    <property name="accessToken" value="${xxl.job.accessToken}" />
</bean>
```

#### Step 4:deploy executor project
You can compile and package the project If have done all the steps above successfully,the project supply two executor demo projects,you can choose any one to deploy:

    xxl-job-executor-sample-spring:compile and package in WAR,can be deployed to tomcat;
    xxl-job-executor-sample-springboot:compile and package in JAR,and run in springboot mode;

Now you have deployed the executor project.

#### Step 5:executor cluster(optional)
In order to improve system availability and job process capacity,executor project can be deployed as cluster.

Prerequisites:keep all node’s configuration item "xxl.job.admin.addresses" exactly the same with each other,all executors can be register automatically. 


### 2.5 Start first job "Hello World"      
Now let’s create a "GLUE模式(Java)" job,if you want to learn more about it , please see “chapter 3：Task details”。( "GLUE模式(Java)"'s code is maintained online through xxl-job-admin,compare with "Bean模式任务" it’s not need to develop, deploy the code on the executor and it’s not need to restart the executor, so it’s lightweight）

#### Prerequisites:please confirm xxl-job-admin and executor project has been deployed successfully.

#### Step 1:Create new job
Login in xxl-job-admin,click on the"新建任务" button, configure the job params as follows and click "保存" button to save the job info.

![task management](https://www.xuxueli.com/doc/static/xxl-job/images/img_o8HQ.png "task management")

![create task](https://www.xuxueli.com/doc/static/xxl-job/images/img_ZAsz.png "create task")

#### Step 2：develop “GLUE模式(Java)” job
Click “GLUE” button on the right of the job to go to GLUE editor view as shown below。“GLUE模式(Java)” mode task has been inited with default task code for printing Hello World。 （ “GLUE模式(Java)” mode task is a java code fragment implements IJobHandler interface,it will be executed in executor,you can use @Resource/@Autowire to inject other java bean instance,if you want to see more info please go to chapter 3）

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Fgql.png "在这里输入图片标题")

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_dNUJ.png "在这里输入图片标题")

#### Step 3:trigger task
If you want to run the job manually please click "执行" button on the right of the job(usually we trigger job by Cron expression)

#### Step 4:view log 
Click “日志” button on the right side of the task you will go to the task log list ,you will see the schedule history records of the task and the schedule detail info,execution info and execution params.If you click the “执行日志” button on the right side of the task log record,you will go to log console and view the execute log in the course of task execution.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_inc8.png "在这里输入图片标题")

On the log console,you can view task execution log on the executor immediately after it dump to log file,so you can monitor the task execution process by Rolling way.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_eYrv.png "在这里输入图片标题")

## 3. Task details

### Description of configuration item:

    - 执行器：the container where job executed in,it will be discovered automaticly if it has registered success when job was scheduled,and the job will be executed automaticly through this way.On the other side all tasks was grouped by this way.Tasks must be binded to a executor and it can be configured on "执行器管理"  page;
    - 描述：the decription of task
    - 路由策略：when executors deployed as a cluster,it can configure multi route policys,include:
        FIRST（第一个）：default select the first executor;
        LAST（最后一个）：default select the last executor;
        ROUND（轮询）：round select the executor;；
        RANDOM（随机）：random select the executor;
        CONSISTENT_HASH（一致性HASH）：all jobs was evenly scheduled on different machines,make sure load balance of executors under the same group and the same job will be scheduled to the same machine.
        LEAST_FREQUENTLY_USED（最不经常使用）：default select the least often used executor.
        LEAST_RECENTLY_USED（最近最久未使用）：defalut select the longest not used executor.
        FAILOVER（故障转移）：beat with the executor in order and select the first beat success executor as target executor.
        BUSYOVER（忙碌转移）：check the executor busy or not in order,the first executor checked not busy is to be select as the target scheduled executor.
        SHARDING_BROADCAST(分片广播)：broadcast all executor nodes under the same executor group execute the job, slice number will be transferred at the same time,shard task will be executed accordate with the shard number.
        
    - Cron：Cron expression used to trigger job execution;
    - 运行模式：
        BEAN模式：job was maintained on the side of executor by  as JobHandler instance,it will be executed accordate with "JobHandler" properties.
        GLUE模式(Java)：task source code is maintened in the schedule center,it must implement IJobHandler and explain by "groovy" in the executor instance,inject other bean instace by annotation @Resource/@Autowire.
        GLUE模式(Shell)：it’s source code is a shell script and maintained in the schedule center.
        GLUE模式(Python)：it’s source code is a python script and maintained in the schedule center.
    - JobHandler：it’s used in  "BEAN模式",it’s instance is defined by annotation @JobHandler on the JobHandler class name.
    - 子任务Key：every task has a unique key (task Key can acquire from task list)，when main task is done successfully it’s child task stand for by this key will be scheduled.
    - 阻塞处理策略：the stategy handle the task when this task is scheduled too frequently and the task is block to wait for cpu time.
        单机串行（默认）：task schedule request go into the FIFO queue and execute serially.
        丢弃后续调度：the schedule request will be discarded and marked as fail when the same task’s  instance scheduled befor is running in the target executor.
        覆盖之前调度：the schedule request will be executed and clear before task queue when the same task’s  instance scheduled befor is running in the target executor.
    - 失败处理策略:handle policy for schedule fail
        失败告警（默认）：it will trigger alarm such as send alarm mail when it’s scheduled fail.
        失败重试：it will try another time when it’s scheduled fai,if try fail it will trigger alarm for fail.every time it will trigger a new schedule request.
    - 执行参数：the params needed in the run time of the task, multiple values are separated by commas,it will be passed to task instace as an array when task is scheduled. 
    - 报警邮件：the email used to receive the alarm mail when task is scheduled fail or execute fail, multiple values are separated by commas.
    - 负责人：The person name response for the task.
    
### 3.1 BEAN模式
The task logic exist in the executor project as JobHandler,the develop steps as shown below:

#### Step 1:develp obHandler in the executor project
    - 1, create new java class implent com.xxl.job.core.handler.IJobHandler;
    - 2, if you add @Component annotation on the top of the class name it’s will be managed as a bean instance by spring container;
    - 3, add  “@JobHandler(value=" customize jobhandler name")” annotation，the value stand for JobHandler name,it will be used as JobHandler property when create a new task in the schedule center.


#### Step 2:create task in schedule center
If you want learn more about configure item please go and sedd “Description of configuration item”，select  "BEAN模式" as run mode，property JobHandler please fill in the value defined by @JobHande.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_ZAsz.png "在这里输入图片标题")

### 3.2 GLUE模式(Java)
Task source code is maintained in the schedule center and can be updated by Web IDE online, it will be compiled and effective real-time,didn’t need to assign JobHandler,develop flow shown as below:

#### Step 1:create task in schedule center
If you want learn more about configure item please go and sedd “Description of configuration item”，select "GLUE模式(Java)" as run mode.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_tJOq.png "在这里输入图片标题")

#### Step 2:develop task source code
Select the task record and click “GLUE” button on the righe of it,it will go to GLUE task’s WEB IDE page,on this page yo can edit you task code(also can edit in other IDE tools,copy and paste into this page).

Version backtrack（support 30 versions while backtrack）：on the WEB IDE page of GLUE task,on upper right corner drop down box please select “版本回溯”,it will display GLUE updated history,select the version you want it will display the source code of this version,it will backtrace the version while click save button. 

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_dNUJ.png "在这里输入图片标题")

### 3.3 GLUE模式(Shell)

#### Step 1:create new task in schedule center  
If you want learn more about configure item please go and sedd “Description of configuration item”，select "GLUE模式(Shell)"as run mode.

#### Step 2:develop task source code
Select the task record and click “GLUE” button on the righe of it,it will go to GLUE task’s WEB IDE page,on this page yo can edit you task code(also can edit in other IDE tools,copy and paste into this page).

Actually it is a shell script fragment.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_iUw0.png "在这里输入图片标题")

### 3.4 GLUE模式(Python)

#### Step 1:create new task in schedule center  
If you want learn more about configure item please go and sedd “Description of configuration item”，select "GLUE模式(Python)"as run mode.

#### Step 2:develop task source code
Select the task record and click “GLUE” button on the righe of it,it will go to GLUE task’s WEB IDE page,on this page yo can edit you task code(also can edit in other IDE tools,copy and paste into this page).

Actually it is a python script fragment.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_BPLG.png "在这里输入图片标题")


## 4. Task Management
### 4.0 configure executor
click"执行器管理" on the left menu,it will go to the page as shown below:
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Hr2T.png "在这里输入图片标题")

    1,"调度中心OnLine”:display schedule center machine list,when task is scheduled it will callback schedule center for notify the execution result in failover mode, so that it can avoid a single point scheduler;
    2,"执行器列表" :display all nodes under this executor group.

If you want to create a new executor,please click "+新增执行器" button: 
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_V3vF.png "在这里输入图片标题")

### Description of executor attributes

    Appname: the unique identity of the executor cluster,executor will registe automatically and periodically by appname so that it can be scheduled.
    名称: the name of ther executor,it is used to describe the executor.
    排序: the order of executor,it will be used in the place where need to select executor.
    注册方式:which way the schedule center used to acquire executor address through;
        自动注册:executor will register automatically,through this schedule center can discover executor dynamically.
        手动录入:fill in executor address manually and it will be used by schedule center, multiple address separated by commas. 
    机器地址:only effective when "注册方式" is "手动录入",support fill in executor address manually.

### 4.1 create new task
Go to task management list page,click “新增任务” button on the upper right corner，on the pop-up window“新增任务”page configure task property and save.learn more info please go and see "3,task details".

### 4.2 edit task
Go to task management list page and choose the task you want to edit ,click”编辑”button on the right side of the task,on the pop-up window “编辑任务”page edit task property and save.

### 4.3 edit GLUE source code

Only fit to GLUE task.

choose the task you want to edit and click” GLUE”button on the right side of the task, it will go to the Web IDE page of GLUE task,then you can edit task source code on this page.you can read "3.2 GLUE模式(Java)" for more info.

### 4.4 pause/recover task
You can pause or recover task but it just fit to follow up schedule trigger and won’t affect scheduled tasks,if you want to stop tasks which has been triggered,please go and see “4.8 stop the running task”

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_ZAhX.png "在这里输入图片标题")

### 4.5 manually trigger
You can trigger a task manually by Click “执行”button,it won’t affect original scheduling rules.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Z5wp.png "在这里输入图片标题")

### 4.6 view schedule log
You can view task’s history schedule log by click “日志” button,on the history schedule log list page you can view every time of task’s schedule result,execution result and so on,click “执行日志” button can view the task’s full execute log.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_9235.png "在这里输入图片标题")

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_inc8.png "在这里输入图片标题")

    调度时间:schedule center trigger time when schedule and send execution signal to executor;
    调度结果:schedule center trigger task’s result, 200 represent success,500 or other number stands for fail;
    调度备注:schedule center trigger task’s remark info;
    执行器地址:the machine address where the task was executed;
    运行模式:run mode of triggered task,go and see  "3,Task Details" for more info;
    任务参数:the input params of the executed task;
    执行时间:the callback time task was done in the executor;
    执行结果:task’s execute result in the executor, 200 represent success,500 or other number stands for fail;
    执行备注:task’s execute remark info in the executor;
    操作:
        "执行日志"button:click this button you can view task’s execution detail log,go and see chapter 4.7 “view execution log” for more info;
        "终止任务"button:click this button you can stop the task’s execution thread on this executor,include bloked task instance which didn’t has started;

### 4.7 view execution log
Click the “执行日志” button on the right side of the record,you can go to the execution log page,you can view the full execution log of the logic business code, shown as below:

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_tvGI.png "在这里输入图片标题")

### 4.8 stop running tasks
Just fit to running tasks,on the task log list page,click “终止任务” button on the right side of the record, it will send stop command to the executor where the task was executed,finally the task was killed and the task instance execute queue of this task will be clear.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_hIci.png "在这里输入图片标题")

It is implemented by interrupt execute thread, it will trigger InterruptedException.so if JobHandler catch this execuption and handle this exception this function is unavailable.

So if you want stop the running task ,the JobHandler need to handle InterruptedException separately by throw this exception.the right logic is as shown below:
```
try{
    // do something
} catch (Exception e) {
    if (e instanceof InterruptedException) {
        throw e;
    }
    logger.warn("{}", e);
}
```

If JobHandler start child thread,child thread also must not catch InterruptedException,and it should throw exception.


### 4.9 delete execution log
On the task log list page, after you select executor and task, you can click"删除" button on the right side and it will pop-up "日志清理" window,on the pop-up window you can choose different log delete policy,choose the policy you want to execute and click "确定" button it will delele relative logs:
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Ypik.png "在这里输入图片标题")

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_EB65.png "在这里输入图片标题")

### 4.10 delete task
Click the delete button on the right side of the task,the task will be deteted.

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Z9Qr.png "在这里输入图片标题")

## 5. Overall design
### 5.1 Source directory introduction
    - /doc :documentation and material
    - /db :db scripts
    - /xxl-job-admin :schedule and admin center
    - /xxl-job-core :common core Jar
    - /xxl-job-executor-samples :executor，Demo project（you can develop on this demo project or adjust your own exist project to executor project)

### 5.2 configure database
XXL-JOB schedule module is implemented based on Quartz cluster,it’s “database” is extended based on Quartz’s 11 mysql tables.

XXL-JOB custom Quartz table structure prefix(XXL_JOB_QRTZ_).

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_bNwm.png "在这里输入图片标题")

The added tables as shown below:
    - XXL_JOB_QRTZ_TRIGGER_GROUP:executor basic table, maintain the info about the executor;
    - XXL_JOB_QRTZ_TRIGGER_REGISTRY:executor register table, maintain addressed of online executors and schedule center machines.
    - XXL_JOB_QRTZ_TRIGGER_INFO:schedule extend table,it is used to save XXL-JOB schedule extended info,such as task group,task name,machine address,executor,input params of task and alarm email and so on.
    - XXL_JOB_QRTZ_TRIGGER_LOG:schedule log table,it is used to save XXL-JOB task’s histry schedule info,such as :schedule result,execution result,input param of scheduled task,scheduled machine and executor and so on.
    - XXL_JOB_QRTZ_TRIGGER_LOGGLUE:schedule log table,it is used to save XXL-JOB task’s histry schedule info,such as :schedule result,execution result,input param of scheduled task,scheduled machine and executor and so on.

So XXL-JOB database total has 16 tables.

### 5.3 Architecture design
#### 5.3.1 Design target
All schedule behavior has been abstracted into “schedule center” common platform , it dosen’t include business logic and just responsible for starting schedule requests.

All tasks was abstracted into separate JobHandler and was managed by executors, executor is responsible for receiving schedule request and execute the relative JobHandler business.

So schedule and task can be decoupled from each other, by the way it can improve the overall stability and scalability of the system.

#### 5.3.2 System composition
- **Schedule module（schedule center）**:
    it is responsible for manage schedule info,send schedule request accord task configuration and it is not include an business code.schedule system decouple with the task, improve the overall stability and scalability of the system, at the same time schedule system performance is no longer limited to task modules. 
    Support visualization, simple and dynamic management schedule information, include create,update,delete, GLUE develop and task alarm and so on, All of the above operations will take effect in real time，support monitor schedule result and execution log and executor failover.
- **Executor module（Executor）**:
    it is responsible for receive schedule request and execute task logic,task module focuses on the execution of the task, Development and maintenance is simpler and more efficient.
    Receive execution request, end request and log request from schedule center.

#### 5.3.3 Architecture diagram

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Qohm.png "在这里输入图片标题")

### 5.4 Schedule module analysis
#### 5.4.1 Disadvantage of quartz
Quartz is a good open source project and was often as the first choice for job schedule.Tasks was managed by api in quartz cluster so it can avoid some  disadvantages of single quartz instance,but it also has some disadvantage as shown below:
    - problem 1:it is not humane while operate task by call apill.
    - problem 2:it is need to store business QuartzJobBean into database, System Invasion is quite serious.
    - problem 3:schedule logic and couple with QuartzJobBean in the same project,it will lead a problem in case that if schedule tasks gradually increased and task logic gradually increased,under this situation the performance of the schedule system will be greatly limited by business.
XXL-JOB solve above problems of quartz.

#### 5.4.2 RemoteHttpJobBean
Under Quartz develop,task logic often was maintained by QuartzJobBean, couple is very serious.in XXL-JOB"Schedule module" and "task module" are completely decoupled,all scheduled tasks in schedule module use the same QuartzJobBean called RemoteHttpJobBean.the params of the tasks was maintained in the extended tables,when trigger RemoteHttpJobBean,it will parse different params and start remote cal l and it wil call relative remote executor.

This call module is like RPC,RemoteHttpJobBean provide call proxy functionality,the executor is provided as remote service.

#### 5.4.3 Schedule Center HA（Cluster）
It is based on Quartz cluster，databse use Mysql；while QUARTZ task schedule is used in Clustered Distributed Concurrent Environment,all nodes will report task info and store into database.it will fetch trigger from database while execute task,if trigger name and execute time is the same only one node will execute the task.

```
# for cluster
org.quartz.jobStore.tablePrefix = XXL_JOB_QRTZ_
org.quartz.scheduler.instanceId: AUTO
org.quartz.jobStore.class: org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.isClustered: true
org.quartz.jobStore.clusterCheckinInterval: 1000
```

#### 5.4.4 Schedule threadpool
Default threads in the threadpool is 10 so it can avoid task schedule delay because of single thread block.

```
org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount: 10
org.quartz.threadPool.threadPriority: 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true
```

business logic was executed on remote executor in XXL-JOB,schedule center just start one schedule request at every schedule time,executor will inqueue the request and response schedule center immediately. There is a huge difference from run business logic in quartz’s  QuartzJobBean directly，just as Elephants and feathers；

the logic of task in XXL-JOB schedule center is very light and single job average run time alaways under 100ms,（most  is network time consume）.so it can use limited threads to support a large mount of job run concurrently, 10 threads configured above can support at least 100 JOB normal execution.

#### 5.4.5 @DisallowConcurrentExecution
This annotation is not used default by the schedule center of XXL-JOB schedule module, it use concurrent policy default,because RemoteHttpJobBean is common QuartzJobBean,so it greatly improve the capacity of schedule system and decrease the blocked chance of schedule module in the case of multi-threaded schedule.

Every schedule module was scheduled and executed parallel in XXL-JOB,but tasks in executor is executed serially and support stop task.

#### 5.4.6 misfire
The handle policy when miss the job’s trigger time.
he reason may be:restart service,schedule thread was blocked by QuartzJobBean, threads was exhausted,some task enable @DisallowConcurrentExecution，the last schedule  was blocked and next schedule was missed.

The default value of misfire in quartz.properties as shown below, unit in milliseconds:
```
org.quartz.jobStore.misfireThreshold: 60000
```

Misfire rule:
    withMisfireHandlingInstructionDoNothing:does not trigger execute immediately and wait for next time schedule. 
    withMisfireHandlingInstructionIgnoreMisfires:execute immediately at the first frequency of the missed time.
    withMisfireHandlingInstructionFireAndProceed:trigger task execution immediately at the frequency of the current time.

XXL-JOB’s default misfire rule:withMisfireHandlingInstructionDoNothing

```
CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getJobCron()).withMisfireHandlingInstructionDoNothing();
CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
```

#### 5.4.7 log callback service
When schedule center of the schedule module was deployed as web service, on one side it play as schedule center, on the other side it also provide api service for executor. 

The source code location of schedule center’s “log callback api service” as shown below:
```
xxl-job-admin#com.xxl.job.admin.controller.JobApiController.callback
```

Executor will execute task when it receive task execute request.it will notify the task execute result to schedule center when the task is done. 

#### 5.4.8 task HA（Failover）
If executor project was deployed as cluster schedule center will known all online executor nodes,such as:“127.0.0.1:9997, 127.0.0.1:9998, 127.0.0.1:9999”.

When "路由策略" select "故障转移(FAILOVER)",it will send heart beat check request in order while schedule center start schedule request.  The first alive checked executor node will be selected and send schedule request to it.

“调度备注” can be viewed on the monitor page when schedule success. As shown below: 
![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_jrdI.png "在这里输入图片标题")

“调度备注” will display local schedule route path、executor’s "注册方式"、"地址列表" and task’s "路由策略"。Under "故障转移(FAILOVER)" policy, schedule center take first address to do heartbeat detection, heat beat fail will automatically skip, the second address heart beat fail…… until the third address “127.0.0.1:9999” heart beat success, it was selected as target executor, then send schedule request to target executor, now the schedule process is end wait for the executor’s callback execution result.

#### 5.4.9 schedule log
Every time when task was scheduled in the schedule center it will record a task log, the task log include three part as shown below:

- 任务信息:include executor address、JobHandler and executor params，accord these parameters it can locate specific machine and task code that the task will be executed.
- 调度信息:include schedule time、schedule result and  schedule log  and so on，accord these parameters you can understand some task schedule info of schedule center.
- 执行信息:include execute time、execute result and execute log and so on, accord these parameters you can understand the task execution info in the executor.

Schedule log stands fo single task schedule, attribute description is as follows:
- 执行器地址:machine addresses on which task will be executed.
- JobHandler:JobHandler name of task under Bean module.
- 任务参数:the input parameters of task
- 调度时间:the schedule time started by schedule center.
- 调度结果:schedule result of schedule center,SUCCESS or FAIL.
- 调度备注:remark info of task scheduled by schedule center, such as address heart beat log.
- 执行时间:the callback time when the task is done in the executor.
- 执行结果:task execute result in the executor,SUCCESS or FAIL.
- 执行备注:task execute remark info in the executor,such as exception log.
- 执行日志:full execution log of the business code during execution of the task,go and see “4.7 view execution log”.

#### 5.4.10 Task dependency
principle:every task has a task key in XXL-JOB, every task can configure property “child task Key”,it can match task dependency relationship through task key.

When parent task end execute and success, it will match child task dependency accord child task key, it will trigger child task execute once if it matched child task.

On the task log page ,you can see matched child task and triggered child task’s log info when you “查看”button of “执行备注”,otherwise the child task didin’t execute, as shown beleow:

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_Wb2o.png "在这里输入图片标题")

![输入图片说明](https://www.xuxueli.com/doc/static/xxl-job/images/img_jOAU.png "在这里输入图片标题")

### 5.5 Task "run mode" analysis
#### 5.5.1 "Bean模式" task
Development steps:go and see "chapter 3" . 
principle: every Bean mode task is a Spring Bean instance and it is maintained in executor project’s Spring container. task class nedd to add “@JobHandler(value="name")” annotation, because executor identify task bean instance in spring container through annotation. Task class nedd to implements interface IJobHandler, task logic code in method execute(), the task logic in execute() method will be executed when executor received a schedule request from schedule center.

#### 5.5.2 "GLUE模式(Java)" task
Development steps:go and see "chapter 3" .
Principle : every "GLUE模式(Java)" task code is a class implemets interface IJobHandler, when executor received schedule request from schedule center these code will be loaded by Groovy classloader and instantiate into a Java object and inject spring bean service declared in this code at the same time（please confirm service and class reference in Glue code exist in executor project）, then call the object’s execute() method and execute task logic.

#### 5.5.3 GLUE模式(Shell) + GLUE模式(Python)
Development steps:go and see "chapter 3" .
principle:the source code of script task is maintained in schedule center and script logic will be executed in executor. when script task was triggered, executor will load script source code and generate a script file on the machine where executor was deployed, the script will be called by java code, the script output log will be written to the task log file in real time so that we can monitor script execution in real time through schedule center, the return code 0 stands for success other for fail.

All supported types of scripts as shown beloes:

    - shell script:shell script task will be enabled when select "GLUE模式(Shell)"as task run mode.
    - python script: python script task will be enabled when select " GLUE模式(Python)"as task run mode.
    

#### 5.5.4 executor
Executor is actually an embedded Jetty server with default port 9999, as shown below（parameter:xxl.job.executor.port）.

Executor will identify Bean mode task in spring container through @JobHandler When project start, it will be managed use the value of annotation as key. 

When executor received schedule request from schedule center, if task type is “Bean模式” it will match bean mode task in Spring container and call it’s execute() method and execute task logic. if task type is “GLUE模式”, it will load Glue code, instantiate a Java object and inject other spring service（notice: the spring service injected in Glue code must exist in the same executor project）, then call execute() method and execute task logic. 

#### 5.5.5 task log
XXL-JOB will generate a log file for every schedule request, the log info will be recorded by XxlJobLogger.log() method, the log file will be loaded when view log info through schedule center.

(history version is implemented by overriding LOG4J’s Appender so it exists dependency restrictions, The way has been discraded in the new version)

The location of log file can be specified in executor configuration file, default pattern is : /data/applogs/xxl-job/jobhandler/formatted date/primary key for database scheduling log records.log”.

When start child thread in JobHandler, child thread will print log in parent JobHandler thread’s execute log in order to trace execute log.

### 5.6 Communication module analysis

#### 5.6.1 A complete task schedule communication process
    - 1,schedule center send http request to executor, and the service in executor in fact is a jetty server with default port 9999.
    - 2,executor execute task logic.
    - 3,executor http callback with schedule center for schedule result, the service in schedule center used to receive callback request from executor is a set of api opended to executor.

#### 5.6.2 Encrypt Communication data
When scheduler center send request to executor, it will use RequestModel and ResponseModel object to encapsulate schedule request parameters and response data, these two object will be serialized before communication, data protocol and time stamp will be checked so that achieve data encryption target.

### 5.7 task register and task auto discover  
Task executor machine property has been canceled from v1.5, instead of task register and auto discovery, get remote machine address dynamic.

    AppName: unique identify of executor cluster,  executor is minimal unite of task register, every task recognize machine addresses under the executor on which it was binded.
    Beat: heartbeat cycle of task register, default is 15s, and the time executor usedto register is twice the time, the time used to auto task discover is twice the beat time, the invalid time of register is twice the Beat time.
    registry table: see XXL_JOB_QRTZ_TRIGGER_REGISTRY table, it will maintain a register record periodically while task register, such as the bind relationship between machine address and AppName, so that schedule center can recognize machine list by AppName dynamicly.

To ensure system lightweight and reduce learning costs, it did not use Zookeeper as register center, Use DB as register center to do task registration.

### 5.8 task execute result
Since v1.6.2, the task execute result is recognized through ReturnT of IJobHandler, it executes success when return value meets the condition "ReturnT.code == ReturnT.SUCCESS_CODE" , or it executes fail, and it can callback error message info to schedule center through ReturnT.msg, so it can control task execute results in the task logic.

### 5.9 slice broadcat & dynamic slice   
When “分片广播” is selected as route policy in executor cluster, one task schedule will broadcast all executor node in cluster to trigger task execute in every executor, pass slice parameter at the same time, so we can develop slice task by slice parameters. 

"分片广播"  break the task by the dimensions of executor, support dynamic extend executor cluster so that it can add slice number dynamically to do business process, In case of large amount of data process can significantly improve task processing capacity and speed.

The develop process of "分片广播" is the same as general task, The difference is that you can get slice parameters，code as shown below（go and see ShardingJobHandler in execuotr example ):

    ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
    
This slice parameter object has two properties:

    index:the current slice number(start with 0)，stands for the number of current executor in the executor cluster.
    total:total slice number,stands for total slices in the executor cluster.

This feature applies to scenes as shown below:
- 1、slice task scene:when 10 executor to handle 10w records,  1w records need to be handled per machine, time-consuming 10 times lower；
- 2、Broadcast task scene:broadcast all cluster nodes to execute shell script、broadcast all cluster nodes to update cache.

### 5.10 AccessToken
To improve system security it is need to check security between schedule center and executor, just allow communication between them when AccessToken of each other matched.

The AccessToken of scheduler center and executor can be configured by xxl.job.accessToken.

There are only two settings when communication between scheduler center and executor just:

- one:do not configure AccessToken on both, close security check.
- two:configure the same AccessToken on both;

### 5.11 Dispatching center API services
The scheduling center provides API services for executors and business parties to choose to use, and the currently available API services are available.

    1. Job result callback service;
    2. Executor registration service;
    3. Executor registration remove services;
    4. Triggers a single execution service, and support the task to be triggered according to the business event;

The scheduling center API service location: com.xxl.job.core.biz.AdminBiz.java

The scheduling center API service requests reference code：com.xxl.job.adminbiz.AdminBizTest.java


## 6 Version update log
### 6.1 version V1.1.x，New features [2015-12-05]
**【since V1.1.x，XXL-JOB was used by company hiring me，alias Ferrari inner company，the latest version is recommended for new project】**
- 1、simple:support CRUD operation through Web page, simple and one minute to get started;
- 2、dynamic:support dynamic update task status,pause/recover task and effective in real time;
- 3、service HA:task info stored in mysql, Job service support cluster to make sure service HA;
- 4、task HA:when some Job services hangs up, tasks will be assigned to some other alive machines, if all nodes of the cluster hangs up,  it will  compensate for the execution of lost task when restart;
- 5、one task instance will only be executed on one executor;
- 6、task is executed serially;
- 7、support for custom parameters;
- 8、Support pause task execution remotely .

### 6.2 version V1.2.x，New features [2016-01-17]
- 1、support task group;
- 2、suport local task, remote task;
- 3、support two types underlying communication ,Servlet or JETTY;
- 4、support task log;
- 5、support serially execution，parallel execution;
	
	Description:system architecture of V1.2 divided by function as shown below:
	
		- schedule module（schedule center）:Responsible for managing schedule information，send schedule request according to the schedule configuration;
		- execute module（executor）:Responsible for receiving schedule request and execute task logic;
		- communication module:Responsible for the communication between the schedule module and execute module;
	advantage:
	
		- Decouple:execute module supply task api, schedule module maintains schedule information, The business is independent of each other;
		- High scalability;
		- stability;

### 6.3 version V1.3.0，New features [2016-05-19]
- 1、discard local task module, remote task was recommended, easy to decouple system, the JobHandler of task was called executor.
- 2、dicard underlying communication type servlet, JETTY was recommended, schedule and callback bidirectional communication, rebuild the communication logic;
- 3、UI interactive optimization:optimize left menu expansion and menu item selected status , task list opens the table with compression optimization;
- 4、【important】executor is subdivided into two develop mode:BEAN、GLUE:
	
	Introduction to the executor mode:
		- BEAN mode executor:every executor is a Spring Bean instance，it was recognized and scheduled by XXL-JOB through @JobHandler annotation;
		 -GLUE mode executor:every executor corresponds to a piece of code，edited and maintained online by Web, Dynamic compile and takes effect in real time, executor is responsible for loading GLUE code and executing;

### 6.4 version V1.3.1，New features [2016-05-23]
- 1、Update project directory structure:
	- /xxl-job-admin -------------------- 【schedule center】:Responsible for managing schedule information，send schedule request according to schedule configuration;
	- /xxl-job-core -----------------------  Public core dependence
	- /xxl-job-executor-example ------ 【executor】:Responsible for receiving scheduling request and execute task logic;
	- /db ---------------------------------- create table script
	- /doc --------------------------------- user manual
- 2、Upgrade the user manual under the new directory structure;
- 3、Optimize some interactions and UI;

### 6.5 version V1.3.2，New features [2016-05-28]
- 1、Schedule logic for transactional handle;
- 2、executor asynchronous callback execution log;
- 3、【important】based on HA support of schedule center，extend executor’s Failover support，Support configure multiple execution addresses;

### 6.6 version V1.4.0 New features [2016-07-24]
- 1、Task dependency: it is implemented by trigger event, it will automatically trigger a child task schedule after Task execute success and callback, multiple child tasks are separated by commas;
- 2、executor source code has been reconstructed, optimize underlying db script;
- 3、optimize task thread group logic of executor, before it is group by executor’s JobHandler so when multiple task reuse Jobhanlder will cause block with each other. Now it is grouped by task of schedule center so tasks are isolated from task execution.
- 4、optimize communication scheme between executor and schedule center, a simple RPC protocol was implemented through Hex + HC, optimize the maintenance and analysis process of communication parameters.
- 5、schedule center, create/edit task, page attribute adjustment:
    - 5.1、the property JobName was removed from task add/edit page and it is changed to automatically generate by system: this field before is used to identify a task in schedule center and did not use in other scenes, so remove it to simplify the task creation;
    - 5.2、adjust "GLUE模式" property in task add/edit page to near JobHandler input box;
    - 5.3、"报警阈值" property was removed from task add/edit page;
    - 5.4、"子任务Key" property was removed from task add/edit page, the key of task can be acquired from task list page, child task will be triggered by child task key when main task execute success.
- 6、bug fix:
    - 6.1、optimize jetty executor shutdown,  solve one problem may cause jetty could not shutdown. 
    - 6.2、optimize callback of executor task queue when task execute finish. Solve a problem which may cause task could not callback.
    - 6.3、Optimize Page List Parameters of Schedule Center, solve one problem which may be caused by post length limit of server.
    - 6.4、optmize executor Jobhandler annotation, solve a problem that container could not load the JobHandler caused by the transaction proxy.
    - 6.5、optimize remote schedule, disable retry policy, solve a problem may caused repeat call;

Tips: V1.3.x release has been published , enter the maintenance phase, branch  address is [V1.3](https://github.com/xuxueli/xxl-job/tree/v1.3) .New features will be updated continuously in the master branch.

### 6.7 version V1.4.1 New features [2016-09-06]
- 1、project successfully pushed to maven central warehouse, Central warehouse address and dependency  as shown below:
    ```
    <!-- http://repo1.maven.org/maven2/com/xuxueli/xxl-job-core/ -->
    <dependency>
        <groupId>com.xuxueli</groupId>
        <artifactId>xxl-job-core</artifactId>
        <version>${最新稳定版}</version>
    </dependency>
    ```
- 2、To adapt to the rules of central warehouse, groupId has been changed from com.xxl to com.xuxueli.
- 3、to resolve the problem that sub-modules can not be compiled separately, system version is not maintained in the project root pom, each sub-module is configured separately for version configuration;
- 4、optimize data byte length statistics rule of RPC communication it may reduce 50% of data traffic;
- 5、IJobHandler cancel task return value, before the execution status is judged by the return value, now it instead of task was executed successfully by default only when exception was caught the task execution was judged failed.
- 6、optimize system public pop-up box as a plugin;
- 7、optimize table structure and the table name now is upper case;
- 8、modify ContentType of JSON response from exception handler of schedule center to fix the bug that it is could not recognized by browser.

### 6.8 version V1.4.2 New features [2016-09-29]
- 1、push V1.4.2 to maven central warehouse, main version V1.4 enter maintenance phase;
- 2、fix problem task list offset when add task;
- 3、fix a style disorder problem that caused by bootstrap does not support the modal frame overlap , the problem occurs when the task is edited;
- 4、optimize schedule status when schedule timeout and Handler could not matched;
- 5、the task could not stop problem caused by catch exception has given solution;

### 6.9 version V1.5.0 New features [2016-11-13]
- 1、task register: executor registers the task automatically, schedule center will automatically discover the registered task and trigger execution.
- 2、add parameter AppName for executor: AppName is the unique identifier of each executor cluster, register periodically and automatically with AppName.
- 3、add column executor management in schedule center : manage online executors, automatically discover registered executors via the property AppName。Only managed executors are allowed to be used;
- 4、change Task group attribute to executor : each task needs to be bound to the specified exector, schedule address is obtained by binded executor;
- 5、discard property task machine: by the way of binding task with executor, automatically discovers registered remote executor address and triggers schedule request.
- 6、add DBGlueLoader in public dependency, it implement GLUE source code calssloader based on native jdbc, Reduce third party reliance (mybatis,spring-orm etc); simplify and optimize executor configuration (for GLUE task), Reduce the difficulty of getting started;
- 7、adjust table structure, reconstruct the project;
- 8、schedule center automatically registered and found, failover: schedule center periodically registered automatically, task callback can recognize all online schedule center addresses, task callback support failover so that it can avoid single point of risk.

### 6.10 version V1.5.1 New features [2016-11-13]
- 1、Reconstruct the underlying code and optimize logic, clean POM and Clean Code;
- 2、Servlet/JSP Spec selected 3.0/2.2;
- 3、Spring updated to 3.2.17.RELEASE version;
- 4、Jetty updated to version 8.2.0.v20160908;
- 5、has push V1.5.0 and V1.5.1 to maven central warehouse;

### 6.10 version V1.5.2 New features [2017-02-28]
- 1、optimize IP tools class which used to gets IP address，IP static cache;
- 2、both executor and schedule center support customize registered IP address;Solve problem when machine has multiple network card and get the wrong card;
- 3、solve the problem that it will generate multiple log files when executed across days;
- 4、the non-sensitive log level is adjusted to debug;
- 5、Upgrade the database connection pool to c3p0;
- 6、optimize log4j property of executor，remove invalid attribute;
- 7、reconstruct underlying code and optimize logic and Clean Code;
- 8、optimize Dependency Injection Logic of GLUE, support injected as alias;

### 6.11 version V1.6.0 New features [2017-03-13]
- 1、upgrade communication scheme，the HEX communication model is adjusted to the B-RPC model based on HTTP;
- 2、executor supports set execution address list manually，provide switch to use automatically registered address or manually set address;
- 3、executor route rules:第一个、最后一个、轮询、随机、一致性HASH、最不经常使用、最近最久未使用、故障转移;
- 4、unified thread model and thread destruction scheme (by the way of listener or stop() method，Destroy the thread when container is destroyed;Daemon is sometimes not ideal);
- 5、unified system configuration data，Unified managed by configuration files;
- 6、CleanCode，Clean up invalid historical parameters;
- 7、extend data structure and adjust related table structure;
- 8、new created task defaults to a non-running state;
- 9、optimize update logic of GLUE mode task instance , The original update is based on the timeout value and now is updated according to the version number，version number plus one while source changed;

### 6.12 version V1.6.1 New features [2017-03-25]
- 1、Rolling log;
- 2、reconstruct WebIDE interactive;
- 3、enhanced communication check，filter unnormal requests effectively;
- 4、enhanced permission check，Using dynamic login TOKEN（recommend instead of internal SSO）;
- 5、optimize database configuration，solve garbled problem;

### 6.13 version V1.6.2 New features [2017-04-25]
- 1、execution report:support view run time data in real time, such as task number, total schedule number, executor number etc., include schedule report , such as scheduled distribution graph on date, scheduled success distribution graph etc;
- 2、JobHandler support set return value for tasks, it is easy to control task execute result in task logic;
- 3、the problem could not view exception info when resource path include space or chinese word casused resource file could not be loaded;
- 4、optimize route policy:fix problems that Loop and LFU routing policy counters are no limit and first route is focused on the first machine;

### 6.14 version V1.7.0 New features [2017-05-02]
- 1、script task:support develop and run script task by GLUE, include script type such as Shell、Python and Groovy;
- 2、add spring-boot type executor example project;
- 3、upgrade jetty to version 9.2;
- 4、task execute log remove log4j dependency, instead of self-realization，Thus eliminate the dependency on the log component;
- 5、executor remove GlueLoader dependency，instead of push mode，thus GLUE source code load no longer rely on JDBC;
- 6、get the project name when login and redirect, solve 404 problem when it is not deployed by the directory;

### 6.15 version V1.7.1 New features [2017-05-08]
- 1、unified write and read code of execute log as UTF-8，solve log garbled problem under windows environment;
- 2、communication timeout period is limited to 10s，To avoid schedule thread is occupied under abnormal situation;
- 3、adjust executor , server stat, destroy and register logic;
- 4、optimize Jetty Server shutdown logic, repair port occupation caused by executor could not be closed normally and frequent printe c3p0 log probleam;
- 5、start child thread in JobHandler，support child thread print execute log and view by Rolling;
- 6、task log cleanup;
- 7、pop-up component is replaced by layer;
- 8、upgrade quartz to version 2.3.0;

### 6.16 version V1.7.2 New features [2017-05-17]
- 1、block handle policy:the policy when schedule is too frequently and the executor it too late to handle, include multiple strategies:single machine serially execute（default）、discard subsequent schedule、override before schedule;
- 2、fail handle policy:handle policy when scheduled fail, include :failure alarm（default）、failed to retry;
- 3、The communication timeout is adjusted to 180s;
- 4、executor and database are completely decoupled，But the executor needs to configure schedule center cluster address。schedule center provides APIs for executor callbacks and heartbeat registration services，cancel jetty inner schedule center, heartbeat cycle is adjusted to 30s，heartbeat failure is triple heartbeat;
- 5、fix executor parameters lost bug when edit;
- 6、add task test Demo to make task logic test easier;

### 6.17 version V1.8.0 New features [2017-07-17]
- 1、optimize update logic of task Cron，instead of rescheduleJob，at the same time preventing set cron repeatedly;
- 2、optimize API callback service failed status code，facilitate troubleshooting;
- 3、XxlJobLogger support multi-parameter;
- 4、route policy add "忙碌转移" mode:Perform idle detection in sequence，The first idle test successfully machine is selected as the target executor and trigger schedule;
- 5、reconstruct route policy code;
- 6、fix executor repeat registration problem;
- 7、Task thread will be destroyed after 30 times idle turn, reduce the inefficient thread consumption of low frequency tasks;
- 8、Executor task execution result batch callback so that reduce callback frequency to improve actuator performance;
- 9、cancle XML configuration of springboot executor project，instead of class configuration;
- 10、supports filter execute log based on running status;
- 11、optimize scheduling Center Task Registration Detection Logic;

### 6.18 version V1.8.1 New features [2017-07-30]
- 1、slice broadcast task:When slice broadcast is selected as route policy in executor cluster, one task schedule will broadcast all executor node in cluster to trigger task execute in every executor, pass slice parameter at the same time, so we can develop slice task by slice parameters;
- 2、dynamic slice: break the task by the dimensions of executor, support dynamic extend executor cluster so that it can add slice number dynamically to do business process, In case of large amount of data process can significantly improve task processing capacity and speed;
- 3、executor JobHandler disables name conflicts;
- 4、executor cluster address list for natural sorting;
- 5、add test cases and optimize DAO layer code for Scheduling center;
- 6、schedule Center API service change to self-study RPC framework to u nify communication model;
- 7、add schedule center API service test Demo, convenient in dispatch center API extension and testing;
- 8、Task list page interaction optimization，The task list is automatically refreshed when the executor group is replaced，create new job defaults to locate current executor position;
- 9、access Token:To improve system security，it is used for safety check between schedule center and executor, communication allowed just when Both Access Token matched;
- 10、upgrade springboot version to 1.5.6.RELEASE of executor;
- 11、unify maven version dependency management;

### 6.19 version V1.8.2 New features[Coding]
- 1,support configuring the HTTPS for executor callback URL;
- 2,Standardize project directory for extend multi executors;
- 3,add JFinal type executor sample project;

### TODO LIST
- 1,Task privilege management:control privilege on executor, check privilege on core operations;
- 2,Task slice routing:using consistent Hash algorithm to calculate slice order as stable as possible, even if there is fluctuation in the registration machine will not cause large fluctuations in the order of slice. Currently using IP natural sorting can meet the demand，to be determined;
- 3,Failure retry optimization:The current failure to retry logic is execute the request logic once again after the scheduled request fails。The optimization point is retry for both scheduling and execution failures, retry a full schedule when retrying，This may lead schedule failure to an infinite loop，to be determined;
- 4,write file when callback failed，read the log when viewing the log，callback confirm after rebooting;
- 5,Task dependency，flow chart，child task + aggregation task，log of each node;
- 6,Scheduled task priority;
- 7,Remove quartz dependencies and rewrite scheduld module:insert the next execution record into delayqueue when add or resume task, schedule center cluster compete distributed lock，successful nodes bulk load expired delayqueue data and batch execution;
- 8,springboot and docker image，and push docker image to the central warehouse，further realize product out of the box;
- 9,globalization:schedule center interface and Official documents，add English version;
- 10,executor removal:notify schedule center and remove the corresponding execute node when executor is destroyed, improve the timeliness of executor state recognized;

## 7. Other

### 7.1 Contributing
Contributions are welcome! Open a pull request to fix a bug, or open an [Issue](https://github.com/xuxueli/xxl-job/issues/) to discuss a new feature or change.

### 7.2 used records（record just for spread，Product is open source and free of charge）
Record for spread product and product is free and open source. 
Welcome to [check in](https://github.com/xuxueli/xxl-job/issues/1 )on github.

### 7.3 Copyright and License
This product is open source and free, and will continue to provide free community technical support. Individual or enterprise users are free to access and use.

- Licensed under the GNU General Public License (GPL) v3.
- Copyright (c) 2015-present, xuxueli.

---
### Donate
No matter how much the amount is enough to express your thought, thank you very much ：）     [To donate](https://www.xuxueli.com/page/donate.html )
