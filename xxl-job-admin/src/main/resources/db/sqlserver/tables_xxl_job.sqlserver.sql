--
-- XXL-JOB
-- Copyright (c) 2015-present, xuxueli.


CREATE TABLE "XXL_JOB_INFO"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "JOB_GROUP" BIGINT NOT NULL,
    "JOB_DESC" VARCHAR (512) NOT NULL,
    "ADD_TIME"        DATETIME,
    "UPDATE_TIME"     DATETIME,
    "AUTHOR" VARCHAR (125),
    "ALARM_EMAIL" VARCHAR (512),
    "SCHEDULE_TYPE" VARCHAR (100) DEFAULT 'NONE' NOT NULL,
    "SCHEDULE_CONF" VARCHAR (256),
    "MISFIRE_STRATEGY" VARCHAR (100) DEFAULT 'DO_NOTHING' NOT NULL,
    "EXECUTOR_ROUTE_STRATEGY" VARCHAR (100),
    "EXECUTOR_HANDLER" VARCHAR (512),
    "EXECUTOR_PARAM" VARCHAR (1024),
    "EXECUTOR_BLOCK_STRATEGY" VARCHAR (100),
    "EXECUTOR_TIMEOUT" INT DEFAULT 0 NOT NULL,
    "EXECUTOR_FAIL_RETRY_COUNT" INT DEFAULT 0 NOT NULL,
    "GLUE_TYPE" VARCHAR (100) NOT NULL,
    "GLUE_SOURCE" text,
    "GLUE_REMARK" VARCHAR (512),
    "GLUE_UPDATETIME" DATETIME,
    "CHILD_JOBID" VARCHAR (512),
    "TRIGGER_STATUS" INT DEFAULT 0 NOT NULL,
    "TRIGGER_LAST_TIME" BIGINT DEFAULT 0,
    "TRIGGER_NEXT_TIME" BIGINT DEFAULT 0,
    "REMARK" VARCHAR (512),
    PRIMARY KEY ("ID")
);

EXEC sp_addextendedproperty
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE',  @level1name = N'XXL_JOB_INFO',
   	@name = N'MS_Description',@value = N'任务信息表'
    ;

EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'JOB_GROUP',
    @name = N'MS_Description', @value = N'执行器主键ID'
    ;

EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'AUTHOR',
    @name = N'MS_Description', @value = N'作者'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'ALARM_EMAIL',
    @name = N'MS_Description', @value = N'报警邮件'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'SCHEDULE_TYPE',
    @name = N'MS_Description', @value = N'调度类型'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'SCHEDULE_CONF',
    @name = N'MS_Description', @value = N'调度配置，值含义取决于调度类型'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'MISFIRE_STRATEGY',
    @name = N'MS_Description', @value = N'调度过期策略'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_ROUTE_STRATEGY',
    @name = N'MS_Description', @value = N'执行器路由策略'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_HANDLER',
    @name = N'MS_Description', @value = N'执行器任务HANDLER'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_PARAM',
    @name = N'MS_Description', @value = N'执行器任务参数'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_BLOCK_STRATEGY',
    @name = N'MS_Description', @value = N'阻塞处理策略'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_TIMEOUT',
    @name = N'MS_Description', @value = N'任务执行超时时间，单位秒'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'EXECUTOR_FAIL_RETRY_COUNT',
    @name = N'MS_Description', @value = N'失败重试次数'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'GLUE_TYPE',
    @name = N'MS_Description', @value = N'GLUE类型'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'GLUE_SOURCE',
    @name = N'MS_Description', @value = N'GLUE源代码'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'GLUE_UPDATETIME',
    @name = N'MS_Description', @value = N'GLUE更新时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'CHILD_JOBID',
    @name = N'MS_Description', @value = N'子任务ID，多个逗号分隔'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'TRIGGER_STATUS',
    @name = N'MS_Description', @value = N'调度状态：0-停止，1-运行'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'TRIGGER_LAST_TIME',
    @name = N'MS_Description', @value = N'上次调度时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'TRIGGER_NEXT_TIME',
    @name = N'MS_Description', @value = N'下次调度时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_INFO',
    @level2type = N'Column',  @level2name = 'REMARK',
    @name = N'MS_Description', @value = N'备注'
    ;


CREATE TABLE "XXL_JOB_LOG"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "JOB_GROUP" BIGINT NOT NULL,
    "JOB_ID" BIGINT NOT NULL,
    "EXECUTOR_ADDRESS" VARCHAR (512),
    "EXECUTOR_HANDLER" VARCHAR (512),
    "EXECUTOR_PARAM" VARCHAR (1024),
    "EXECUTOR_SHARDING_PARAM" VARCHAR (40),
    "EXECUTOR_FAIL_RETRY_COUNT" INT DEFAULT 0 NOT NULL,
    "TRIGGER_TIME" DATETIME,
    "TRIGGER_CODE" INT NOT NULL,
    "TRIGGER_MSG" TEXT,
    "HANDLE_TIME"  DATETIME,
    "HANDLE_CODE" INT NOT NULL,
    "HANDLE_MSG" TEXT,
    "ALARM_STATUS" INT DEFAULT 0 NOT NULL,
    PRIMARY KEY ("ID")
);

CREATE INDEX "IDX_XXL_JOB_LOG_TRIGGER_TIME" ON "XXL_JOB_LOG" ("TRIGGER_TIME")
;

CREATE INDEX "IDX_XXL_JOB_LOG_HANDLE_CODE" ON "XXL_JOB_LOG" ("HANDLE_CODE")
;

CREATE INDEX "IDX_XXL_JOB_LOG_JOBID_JOBGROUP" ON "XXL_JOB_LOG" ("JOB_ID","JOB_GROUP")
;

CREATE INDEX "IDX_XXL_JOB_LOG_JOB_ID" ON "XXL_JOB_LOG" ("JOB_ID")
;

EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'JOB_GROUP',
    @name = N'MS_Description', @value = N'执行器主键ID'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'JOB_ID',
    @name = N'MS_Description', @value = N'任务，主键ID'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'EXECUTOR_ADDRESS',
    @name = N'MS_Description', @value = N'执行器地址，本次执行的地址'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'EXECUTOR_HANDLER',
    @name = N'MS_Description', @value = N'执行器任务HANDLER'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'EXECUTOR_PARAM',
    @name = N'MS_Description', @value = N'执行器任务参数'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'EXECUTOR_SHARDING_PARAM',
    @name = N'MS_Description', @value = N'执行器任务分片参数，格式如 1/2'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'EXECUTOR_FAIL_RETRY_COUNT',
    @name = N'MS_Description', @value = N'失败重试次数'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'TRIGGER_TIME',
    @name = N'MS_Description', @value = N'调度-时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'TRIGGER_CODE',
    @name = N'MS_Description', @value = N'调度-结果'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'TRIGGER_MSG',
    @name = N'MS_Description', @value = N'调度-日志'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'HANDLE_TIME',
    @name = N'MS_Description', @value = N'执行-时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'HANDLE_CODE',
    @name = N'MS_Description', @value = N'执行-状态'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'HANDLE_MSG',
    @name = N'MS_Description', @value = N'执行-日志'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG',
    @level2type = N'Column',  @level2name = 'ALARM_STATUS',
    @name = N'MS_Description', @value = N'告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败'
    ;


CREATE TABLE "XXL_JOB_LOG_REPORT"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "TRIGGER_DAY" DATETIME,
    "RUNNING_COUNT" INT DEFAULT 0 NOT NULL,
    "SUC_COUNT" INT DEFAULT 0 NOT NULL,
    "FAIL_COUNT" INT DEFAULT 0 NOT NULL,
    "UPDATE_TIME" DATETIME,
    PRIMARY KEY ("ID")
);

CREATE INDEX "IDX_XXL_JOB_LOG_REPORT_TRIGGER_DAY" ON "XXL_JOB_LOG_REPORT" ("TRIGGER_DAY")
;


EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG_REPORT',
    @level2type = N'Column',  @level2name = 'TRIGGER_DAY',
    @name = N'MS_Description', @value = N'调度-时间'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG_REPORT',
    @level2type = N'Column',  @level2name = 'RUNNING_COUNT',
    @name = N'MS_Description', @value = N'运行中-日志数量'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG_REPORT',
    @level2type = N'Column',  @level2name = 'SUC_COUNT',
    @name = N'MS_Description', @value = N'执行成功-日志数量'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOG_REPORT',
    @level2type = N'Column',  @level2name = 'FAIL_COUNT',
    @name = N'MS_Description', @value = N'执行失败-日志数量'
    ;

CREATE TABLE "XXL_JOB_LOGGLUE"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "JOB_ID" BIGINT NOT NULL,
    "GLUE_TYPE" VARCHAR (100),
    "GLUE_SOURCE" TEXT,
    "GLUE_REMARK" VARCHAR (256) NOT NULL,
    "ADD_TIME"    DATETIME,
    "UPDATE_TIME" DATETIME,
    PRIMARY KEY ("ID")
);



EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOGGLUE',
    @level2type = N'Column',  @level2name = 'JOB_ID',
    @name = N'MS_Description', @value = N'任务，主键ID'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOGGLUE',
    @level2type = N'Column',  @level2name = 'GLUE_TYPE',
    @name = N'MS_Description', @value = N'GLUE类型'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOGGLUE',
    @level2type = N'Column',  @level2name = 'GLUE_SOURCE',
    @name = N'MS_Description', @value = N'GLUE源代码'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOGGLUE',
    @level2type = N'Column',  @level2name = 'GLUE_REMARK',
    @name = N'MS_Description', @value = N'GLUE备注'
    ;

CREATE TABLE "XXL_JOB_REGISTRY"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "REGISTRY_GROUP" VARCHAR (100) NOT NULL,
    "REGISTRY_KEY" VARCHAR (512) NOT NULL,
    "REGISTRY_VALUE" VARCHAR (512) NOT NULL,
    "UPDATE_TIME" DATETIME,
    PRIMARY KEY ("ID")
);

CREATE UNIQUE INDEX "IDX_XXL_JOB_REGISTRY_G_K_V" ON "XXL_JOB_REGISTRY" ("REGISTRY_GROUP", "REGISTRY_KEY", "REGISTRY_VALUE")
;


CREATE TABLE "XXL_JOB_GROUP"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "APP_NAME" VARCHAR (128) NOT NULL,
    "TITLE" VARCHAR (128) NOT NULL,
    "ADDRESS_TYPE" INT DEFAULT 0 NOT NULL,
    "ADDRESS_LIST" VARCHAR (4000),
    "UPDATE_TIME" DATETIME,
    PRIMARY KEY ("ID")
);


EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_GROUP',
    @level2type = N'Column',  @level2name = 'APP_NAME',
    @name = N'MS_Description', @value = N'执行器APPNAME'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_GROUP',
    @level2type = N'Column',  @level2name = 'TITLE',
    @name = N'MS_Description', @value = N'执行器名称'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_GROUP',
    @level2type = N'Column',  @level2name = 'ADDRESS_TYPE',
    @name = N'MS_Description', @value = N'执行器地址类型：0=自动注册、1=手动录入'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_GROUP',
    @level2type = N'Column',  @level2name = 'ADDRESS_LIST',
    @name = N'MS_Description', @value = N'执行器地址列表，多地址逗号分隔'
    ;


CREATE TABLE "XXL_JOB_USER"
(
    "ID" BIGINT IDENTITY(100,1) NOT NULL,
    "USERNAME" VARCHAR (100) NOT NULL,
    "PASSWORD" VARCHAR (600) NOT NULL,
    "ROLE" INT NOT NULL,
    "PERMISSION" VARCHAR (512),
    PRIMARY KEY ("ID")
);

CREATE UNIQUE INDEX "IDX_XXL_JOB_USER_USERNAME" ON "XXL_JOB_USER" ("USERNAME")
;



EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_USER',
    @level2type = N'Column',  @level2name = 'USERNAME',
    @name = N'MS_Description', @value = N'账号'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_USER',
    @level2type = N'Column',  @level2name = 'PASSWORD',
    @name = N'MS_Description', @value = N'密码'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_USER',
    @level2type = N'Column',  @level2name = 'ROLE',
    @name = N'MS_Description', @value = N'角色：0-普通用户、1-管理员'
    ;
EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_USER',
    @level2type = N'Column',  @level2name = 'PERMISSION',
    @name = N'MS_Description', @value = N'权限：执行器ID列表，多个逗号分割'
    ;


CREATE TABLE "XXL_JOB_LOCK"
(
    "LOCK_NAME" VARCHAR (100) NOT NULL,
    PRIMARY KEY ("LOCK_NAME")
);


EXEC sys.sp_addextendedproperty
    @level0type = N'Schema',  @level0name = N'dbo',
    @level1type = N'Table',  @level1name = 'XXL_JOB_LOCK',
    @level2type = N'Column',  @level2name = 'LOCK_NAME',
    @name = N'MS_Description', @value = N'锁名称'
    ;

set identity_insert XXL_JOB_GROUP on;

INSERT INTO "XXL_JOB_GROUP"("ID", "APP_NAME", "TITLE", "ADDRESS_TYPE", "ADDRESS_LIST", "UPDATE_TIME")
VALUES (1, 'xxl-job-executor-sample', '通用执行器Sample', 0, NULL, SYSDATETIME());

INSERT INTO "XXL_JOB_GROUP"("ID", "APP_NAME", "TITLE", "ADDRESS_TYPE", "ADDRESS_LIST", "UPDATE_TIME")
VALUES   (2, 'xxl-job-executor-sample-ai', 'AI执行器Sample', 0, NULL, SYSDATETIME());

set identity_insert XXL_JOB_GROUP off;

set identity_insert XXL_JOB_INFO on;

INSERT INTO "XXL_JOB_INFO"("ID", "JOB_GROUP", "JOB_DESC",
                           "ADD_TIME", "UPDATE_TIME",
                           "AUTHOR", "ALARM_EMAIL",
                           "SCHEDULE_TYPE", "SCHEDULE_CONF",
                           "MISFIRE_STRATEGY", "EXECUTOR_ROUTE_STRATEGY",
                           "EXECUTOR_HANDLER", "EXECUTOR_PARAM",
                           "EXECUTOR_BLOCK_STRATEGY",
                           "EXECUTOR_TIMEOUT", "EXECUTOR_FAIL_RETRY_COUNT",
                           "GLUE_TYPE", "GLUE_SOURCE", "GLUE_REMARK",
                           "GLUE_UPDATETIME",
                           "CHILD_JOBID")
VALUES (1, 1, '示例任务01', SYSDATETIME(), SYSDATETIME(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        SYSDATETIME(), '');

INSERT INTO "XXL_JOB_INFO"("ID", "JOB_GROUP", "JOB_DESC",
                           "ADD_TIME", "UPDATE_TIME",
                           "AUTHOR", "ALARM_EMAIL",
                           "SCHEDULE_TYPE", "SCHEDULE_CONF",
                           "MISFIRE_STRATEGY", "EXECUTOR_ROUTE_STRATEGY",
                           "EXECUTOR_HANDLER", "EXECUTOR_PARAM",
                           "EXECUTOR_BLOCK_STRATEGY",
                           "EXECUTOR_TIMEOUT", "EXECUTOR_FAIL_RETRY_COUNT",
                           "GLUE_TYPE", "GLUE_SOURCE", "GLUE_REMARK",
                           "GLUE_UPDATETIME",
                           "CHILD_JOBID")
VALUES (2, 2, 'Ollama示例任务01', SYSDATETIME(), SYSDATETIME(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "慢SQL问题分析思路",
    "prompt": "你是一个研发工程师，擅长解决技术类问题。"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        SYSDATETIME(), '');

INSERT INTO "XXL_JOB_INFO"("ID", "JOB_GROUP", "JOB_DESC",
                           "ADD_TIME", "UPDATE_TIME",
                           "AUTHOR", "ALARM_EMAIL",
                           "SCHEDULE_TYPE", "SCHEDULE_CONF",
                           "MISFIRE_STRATEGY", "EXECUTOR_ROUTE_STRATEGY",
                           "EXECUTOR_HANDLER", "EXECUTOR_PARAM",
                           "EXECUTOR_BLOCK_STRATEGY",
                           "EXECUTOR_TIMEOUT", "EXECUTOR_FAIL_RETRY_COUNT",
                           "GLUE_TYPE", "GLUE_SOURCE", "GLUE_REMARK",
                           "GLUE_UPDATETIME",
                           "CHILD_JOBID")
VALUES (3, 2, 'Dify示例任务', SYSDATETIME(), SYSDATETIME(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"查询班级各学科前三名"
    },
    "user": "xxl-job"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        SYSDATETIME(), '');

set identity_insert XXL_JOB_INFO off;

set identity_insert XXL_JOB_USER on;

INSERT INTO "XXL_JOB_USER"("ID", "USERNAME", "PASSWORD", "ROLE", "PERMISSION")
VALUES (1, 'admin', '$2a$10$rElzP.wCmjyjsVIIqoP4fe8u1qH3otIxiG4UhYs9A3Ivsrm1LrpOu', 1, NULL);

set identity_insert XXL_JOB_USER off;

INSERT INTO "XXL_JOB_LOCK" ("LOCK_NAME")
VALUES ('schedule_lock');

