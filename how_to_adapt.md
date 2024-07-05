# how to adapt your database type?
- there will use adapt oracle as a demo

## step 1
- modify initial ddl sql script
- first, remind your type is ***oracle***
- copy ***doc/db/tables_xxl_job.official.sql*** as your database type
- such as ***doc/db/tables_xxl_job.oracle.sql***
- use ***oracle*** replace ***official***
- modify it to your database grammar, include column type, primary key auto increment,comment,index
- usually, bigint,datetime,text maybe not different
- some database comment not support embed in create table defined
- some database index not support embed in create table defined

## step 2
- copy script to auto initialize resources path
- make directory ***xxl-job-admin/src/main/resources/db/oracle***
- copy file ***doc/db/tables_xxl_job.oracle.sql*** to ***xxl-job-admin/src/main/resources/db/oracle/tables_xxl_job.oracle.sql***

## step 3
- add jdbc driver into ***pom.xml***
- such as
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
- if jdbc driver not in maven repo
- you need copy driver jar into ***xxl-job-admin/lib***
- and add maven into ***pom.xml*** as system scope include
- and, add this jar into classpath
- separator is space, not newline
```shell
xxl-job-admin/pom.xml / build / plugins / maven-jar-plugin / configuration / archive / manifestEntries / Class-Path 
```

## step 4
- add database default config
- add database type into ***application-dev.properties***
- such as
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

## step 5
- make mybatis mapper xml file
- make directory ***xxl-job-admin/src/main/resources/mybatis-mapper/oracle***
- copy files in ***xxl-job-admin/src/main/resources/mybatis-mapper/mysql*** into ***xxl-job-admin/src/main/resources/mybatis-mapper/oracle***
- modify column or table character case while database is case sensitive
- such as, oracle default is upper-case, mysql is lower-case
- attention below locations
```shell
1. resultMap/result.column
2. other column or table name in sql
```
- modify column quote while column is keywords of database
- such as, oracle use "id", mysql use `id` to include column name
- attention below locations
```shell
1. table or column name is include with database keywords
2. usually is "" or `` include column or table name
```
- modify mybatis selectKey to adapt your database
- such as, oracle use sequence to implement auto increment, and use select nextval in "before" type selectKey
- but, use "after" type selectKey in mysql
- attention below locations
```shell
1. every insert sql
2. XxlJobGroupMapper.xml / save / selectKey
3. XxlJobInfoMapper.xml / save / selectKey
4. XxlJobLogGlueMapper.xml / save / selectKey
5. XxlJobLogMapper.xml / save / selectKey
6. XxlJobLogReportMapper.xml / save / selectKey
7. XxlJobUserMapper.xml / save / selectKey
```
- modify page sql to your database grammar
- such as, oracle use rownum, mysql use limit to implement page sql
- attention below locations
```shell
1. every select sql
2. search in directory of keyword 'limit'
3. XxlJobGroupMapper.xml / pageList
4. XxlJobInfoMapper.xml / pageList
5. XxlJobInfoMapper.xml / scheduleJobQuery
6. XxlJobLogGlueMapper.xml / removeOld
7. XxlJobLogMapper.xml / pageList
8. XxlJobLogMapper.xml / findClearLogIds
9. XxlJobLogMapper.xml / findFailJobLogIds
10. XxlJobUserMapper.xml / pageList
```
- specially, if page not use offset+limit mode
- you need modify or add your ***IDatabasePageableConverter*** into spring context
- such as ***OffsetLimitPageableConverter*** or ***OraclePageableConverter***
- and use those page parameters (size/length) in your mapper xml file
```shell
1. global search in java source files of keyword 'DatabasePlatformUtil.convertPageable('
```
- modify column alias while returnType is Map type
- attention below locations
```shell
1. XxlJobLogMapper.xml / findLogReport
```

## step 6
- run & test
- run this spring application 
```shell
com.xxl.job.admin.XxlJobAdminApplication
```
- open in browser
```shell
http://localhost:8080/xxl-job-admin
```
- login
```shell
admin
123456
```
- add a user, such 'test'
- modify this user 'test'
- logout
- login 'test'
- view every page
- logout
- login 'admin'
- add a executor, any address, such as http://localhost:8081/
- add a job into this executor
- run once of this job
- view run log
- view error detail message
  - because of not exists this executor
  - must cause run error
- view report chart
- if every page are running normal, adapt are finished
- else, view console log and fixed it until no error log found
- error log exclude invoke executor rpc error/ http error

## step 7
- adapt finish & adapt question
- you can reference of oracle/postgre/h2/... those implement logic
- and to adjust your adapt logic
- pay attention to keep original features are runnable
- don't effect original features
