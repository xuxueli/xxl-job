# 怎么适配你的数据库类型？
- 下面将以适配oracle为演示

## 步骤 1
- 修改初始化DDL脚本
- 首先，记住你的数据库类型是 ***oracle***
- 复制 ***doc/db/tables_xxl_job.official.sql*** 为你的数据库类型
- 比如 ***doc/db/tables_xxl_job.oracle.sql***
- 使用 ***oracle*** 替换 ***official***
- 修改这个脚本为你的数据库语法，包含列类型，主键自增，注释，索引
- 通常，bigint,datetime,text 或许会不同
- 一些数据库注释不支持嵌入在 create table 中定义
- 同样，一些数据库索引不支持嵌入在 create table 中定义

## 步骤 2
- 复制脚本到自动初始化资源目录
- 创建目录 ***xxl-job-admin/src/main/resources/db/oracle***
- 复制文件 ***doc/db/tables_xxl_job.oracle.sql*** 到 ***xxl-job-admin/src/main/resources/db/oracle/tables_xxl_job.oracle.sql***

## 步骤 3
- 添加JDBC驱动到 ***pom.xml***
- 例如
```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
</dependency>
<dependency>
    <groupId>com.oracle.database.nls</groupId>
    <artifactId>orai18n</artifactId>
</dependency>
```
- 如果jdbc驱动不再maven仓库中
- 你需要复制驱动jar到 ***xxl-job-admin/lib***
- 并且添加到 ***pom.xml*** 作为 system scope 包含
- 并且， 添加这个jar到classpath
- 分隔符是空，不是换行
```shell
xxl-job-admin/pom.xml / build / plugins / maven-jar-plugin / configuration / archive / manifestEntries / Class-Path 
```

## 步骤 4
- 添加数据库默认配置
- 添加数据库类型到 ***application-dev.properties***
- 例如
```properties
## ############################# oracle ###################################
## database platform, mysql|oracle|postgre|gbase|h2|dm|kingbase
xxl.job.database.platform.type=oracle

### xxl-job, datasource
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:orcl
spring.datasource.username=xxl_job
spring.datasource.password=123456
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

## 步骤 5
- 创建mybatis的mapper-xml文件
- 创建目录 ***xxl-job-admin/src/main/resources/mybatis-mapper/oracle***
- 复制所有文件从 ***xxl-job-admin/src/main/resources/mybatis-mapper/mysql*** 到 ***xxl-job-admin/src/main/resources/mybatis-mapper/oracle***
- 当数据库是大小写敏感时，修改表名和列名为对应的大小写
- 例如, oracle 模式是大写，mysql 模式是小写
- 注意下面的位置
```shell
1. resultMap/result.column
2. SQL语句中的表名和列名
```
- 修改列名包含符号，当列名是数据库关键字时
- 例如, oracle 使用 "id", mysql 使用 `id` 来包含列名
- 注意下面的位置
```shell
1. 表名或列名是数据库关键字
2. 通常是 "" 或 `` 来包含列名或表名
```
- 修改 mybatis 的 selectKey 适应数据库
- 例如, oracle 使用 sequence 实现自增, 并使用 select nextval 在 "before" 类型的 selectKey
- 但是, 使用 "after" 类型的 selectKey 在 mysql 中
- 注意下面的位置
```shell
1. 每一个 insert 插入语句
2. XxlJobGroupMapper.xml / save / selectKey
3. XxlJobInfoMapper.xml / save / selectKey
4. XxlJobLogGlueMapper.xml / save / selectKey
5. XxlJobLogMapper.xml / save / selectKey
6. XxlJobLogReportMapper.xml / save / selectKey
7. XxlJobUserMapper.xml / save / selectKey
```
- 修改分页为你的数据库实现方式
- 例如, oracle 使用 rownum, mysql 使用 limit 来实现分页
- 注意下面的位置
```shell
1. 每一个 select 查询语句
2. 在目录中搜索关键字 'limit'
3. XxlJobGroupMapper.xml / pageList
4. XxlJobInfoMapper.xml / pageList
5. XxlJobInfoMapper.xml / scheduleJobQuery
6. XxlJobLogGlueMapper.xml / removeOld
7. XxlJobLogMapper.xml / pageList
8. XxlJobLogMapper.xml / findClearLogIds
9. XxlJobLogMapper.xml / findFailJobLogIds
10. XxlJobUserMapper.xml / pageList
```
- 特别地, 如果分页不使用 offset+limit 模式
- 你就需要修改或者添加你的 ***IDatabasePageableConverter*** 到 spring context 中
- 例如： ***OffsetLimitPageableConverter*** 或 ***OraclePageableConverter***
- 并且使用这些分页参数（size/length）在你的 mapper-xml 文件中
```shell
1. 全局在java文件中搜索关键字 'DatabasePlatformUtil.convertPageable('
```
- 修改列别名， 当 returnType 是 Map 类型的时候
- 注意下面的位置
```shell
1. XxlJobLogMapper.xml / findLogReport
```

## 步骤 6
- 运行 & 测试
- 运行这个spring应用
```shell
com.xxl.job.admin.XxlJobAdminApplication
```
- 打开浏览器
```shell
http://localhost:8080/xxl-job-admin
```
- 登录
```shell
admin
123456
```
- 添加一个用户, 例如 'test'
- 修改这个用户 'test'
- 登出
- 登录 'test'
- 浏览每个页面
- 登出
- 登录 'admin'
- 添加一个执行器，任意地址，例如 http://localhost:8081/
- 添加一个任务到这个执行器
- 运行一次这个任务
- 查看运行日志
- 查看错误详情
  - 因为不存在这个执行器
  - 一定会运行错误
- 查看运行报表
- 如果每个页面都允许正常，适配就结束了
- 否则, 查看控制台错误，并修复，直到没有错误为止
- 错误日志不包括 rpc / http 调用错误

## 步骤 7
- 适配结束 & 适配问题
- 你可以参考 oracle/postgre/h2/... 这些实现逻辑
- 并调整你的实现逻辑
- 注意保持原来的功能是可运行的
- 不要影响原来的功能
