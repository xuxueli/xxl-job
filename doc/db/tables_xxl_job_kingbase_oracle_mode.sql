CREATE database xxl_job;
\c xxl_job
set client_encoding='UTF8';

--drop table XXL_JOB_INFO;
CREATE TABLE XXL_JOB_INFO
(
    ID                        INT                NOT NULL,
    JOB_GROUP                 INT                NOT NULL,
    JOB_DESC                  VARCHAR(255)       NOT NULL,
    ADD_TIME                  TIMESTAMP,
    UPDATE_TIME               TIMESTAMP,
    AUTHOR                    VARCHAR(64),
    ALARM_EMAIL               VARCHAR(255),
    SCHEDULE_TYPE             VARCHAR(50)        NOT NULL DEFAULT 'NONE',
    SCHEDULE_CONF             VARCHAR(128),
    MISFIRE_STRATEGY          VARCHAR(50)        NOT NULL DEFAULT 'DO_NOTHING',
    EXECUTOR_ROUTE_STRATEGY   VARCHAR(50),
    EXECUTOR_HANDLER          VARCHAR(255),
    EXECUTOR_PARAM            VARCHAR(512),
    EXECUTOR_BLOCK_STRATEGY   VARCHAR(50),
    EXECUTOR_TIMEOUT          INT                NOT NULL DEFAULT 0,
    EXECUTOR_FAIL_RETRY_COUNT INT                NOT NULL DEFAULT 0,
    GLUE_TYPE                 VARCHAR(50)        NOT NULL,
    GLUE_SOURCE               TEXT,
    GLUE_REMARK               VARCHAR(128),
    GLUE_UPDATETIME           TIMESTAMP,
    CHILD_JOBID               VARCHAR(255),
    TRIGGER_STATUS            TINYINT            NOT NULL DEFAULT 0,
    TRIGGER_LAST_TIME         BIGINT             NOT NULL DEFAULT 0,
    TRIGGER_NEXT_TIME         BIGINT             NOT NULL DEFAULT 0,
    PRIMARY KEY (ID)
);

comment on column XXL_JOB_INFO.SCHEDULE_CONF
    is '调度配置，值含义取决于调度类型';
comment on column XXL_JOB_INFO.EXECUTOR_HANDLER
    is '执行器任务handler';
comment on column XXL_JOB_INFO.EXECUTOR_PARAM
    is '执行器任务参数';
comment on column XXL_JOB_INFO.EXECUTOR_BLOCK_STRATEGY
    is '阻塞处理策略';
comment on column XXL_JOB_INFO.EXECUTOR_TIMEOUT
    is '任务执行超时时间，单位秒';
comment on column XXL_JOB_INFO.EXECUTOR_FAIL_RETRY_COUNT
    is '失败重试次数';
comment on column XXL_JOB_INFO.GLUE_TYPE
    is 'GLUE类型';
comment on column XXL_JOB_INFO.GLUE_SOURCE
    is 'GLUE源代码';
comment on column XXL_JOB_INFO.GLUE_REMARK
    is 'GLUE备注';
comment on column XXL_JOB_INFO.GLUE_UPDATETIME
    is 'GLUE更新时间';
comment on column XXL_JOB_INFO.CHILD_JOBID
    is '子任务ID，多个逗号分隔';
comment on column XXL_JOB_INFO.TRIGGER_STATUS
    is '调度状态：0-停止，1-运行';
comment on column XXL_JOB_INFO.TRIGGER_LAST_TIME
    is '上次调度时间';
comment on column XXL_JOB_INFO.TRIGGER_NEXT_TIME
    is '下次调度时间';
comment on column XXL_JOB_INFO.MISFIRE_STRATEGY
    is '调度过期策略';
comment on column XXL_JOB_INFO.SCHEDULE_TYPE
    is '调度类型';
comment on column XXL_JOB_INFO.EXECUTOR_ROUTE_STRATEGY
    is '执行器路由策略';
comment on column XXL_JOB_INFO.AUTHOR
    is '作者';
comment on column XXL_JOB_INFO.ALARM_EMAIL
    is '报警邮件';
comment on column XXL_JOB_INFO.JOB_GROUP
    is '执行器主键ID';

--drop table XXL_JOB_LOG;
CREATE TABLE XXL_JOB_LOG
(
    ID                        BIGINT             NOT NULL,
    JOB_GROUP                 INT                NOT NULL,
    JOB_ID                    INT                NOT NULL,
    EXECUTOR_ADDRESS          VARCHAR(255),
    EXECUTOR_HANDLER          VARCHAR(255),
    EXECUTOR_PARAM            VARCHAR(512),
    EXECUTOR_SHARDING_PARAM   VARCHAR(20),
    EXECUTOR_FAIL_RETRY_COUNT INT                NOT NULL DEFAULT 0,
    TRIGGER_TIME              TIMESTAMP,
    TRIGGER_CODE              INT                NOT NULL,
    TRIGGER_MSG               TEXT,
    HANDLE_TIME               TIMESTAMP,
    HANDLE_CODE               INT                NOT NULL,
    HANDLE_MSG                TEXT,
    ALARM_STATUS              TINYINT            NOT NULL DEFAULT 0,
    PRIMARY KEY (ID)
);

comment on column XXL_JOB_LOG.JOB_GROUP
    is '执行器主键ID';
comment on column XXL_JOB_LOG.JOB_ID
    is '任务，主键ID';
comment on column XXL_JOB_LOG.EXECUTOR_ADDRESS
    is '执行器地址，本次执行的地址';
comment on column XXL_JOB_LOG.EXECUTOR_HANDLER
    is '执行器任务handler';
comment on column XXL_JOB_LOG.EXECUTOR_PARAM
    is '执行器任务参数';
comment on column XXL_JOB_LOG.EXECUTOR_SHARDING_PARAM
    is '执行器任务分片参数，格式如 1/2';
comment on column XXL_JOB_LOG.EXECUTOR_FAIL_RETRY_COUNT
    is '失败重试次数';
comment on column XXL_JOB_LOG.TRIGGER_TIME
    is '调度-时间';
comment on column XXL_JOB_LOG.TRIGGER_CODE
    is '调度-结果';
comment on column XXL_JOB_LOG.TRIGGER_MSG
    is '调度-日志';
comment on column XXL_JOB_LOG.HANDLE_TIME
    is '执行-时间';
comment on column XXL_JOB_LOG.HANDLE_CODE
    is '执行-状态';
comment on column XXL_JOB_LOG.HANDLE_MSG
    is '执行-日志';
comment on column XXL_JOB_LOG.ALARM_STATUS
    is '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';

create index IDX_JOB_LOG_TRIGGER_TIME on XXL_JOB_LOG(TRIGGER_TIME) ;
create index IDX_JOB_LOG_HANDLE_CODE on XXL_JOB_LOG(HANDLE_CODE) ;
create index IDX_JOB_LOG_JOB_ID_GROUP on XXL_JOB_LOG(JOB_ID,JOB_GROUP) ;
create index IDX_JOB_LOG_JOB_ID on XXL_JOB_LOG(JOB_ID) ;

--drop table XXL_JOB_LOG_REPORT;
CREATE TABLE XXL_JOB_LOG_REPORT
(
    ID            INT                NOT NULL,
    TRIGGER_DAY   TIMESTAMP,
    RUNNING_COUNT INT                NOT NULL DEFAULT 0,
    SUC_COUNT     INT                NOT NULL DEFAULT 0,
    FAIL_COUNT    INT                NOT NULL DEFAULT 0,
    UPDATE_TIME   TIMESTAMP,
    PRIMARY KEY (ID)
);
comment on table XXL_JOB_LOG_REPORT
    is '日志报表';
comment on column XXL_JOB_LOG_REPORT.TRIGGER_DAY
    is '调度-时间';
comment on column XXL_JOB_LOG_REPORT.RUNNING_COUNT
    is '运行中-日志数量';
comment on column XXL_JOB_LOG_REPORT.SUC_COUNT
    is '执行成功-日志数量';
comment on column XXL_JOB_LOG_REPORT.FAIL_COUNT
    is '执行失败-日志数量';

CREATE UNIQUE INDEX I_TRIGGER_DAY ON XXL_JOB_LOG_REPORT(TRIGGER_DAY);


--drop table XXL_JOB_LOGGLUE;
CREATE TABLE XXL_JOB_LOGGLUE
(
    ID          INT                NOT NULL,
    JOB_ID      INT                NOT NULL,
    GLUE_TYPE   VARCHAR(50),
    GLUE_SOURCE TEXT,
    GLUE_REMARK VARCHAR(128)       NOT NULL,
    ADD_TIME    TIMESTAMP,
    UPDATE_TIME TIMESTAMP,
    PRIMARY KEY (ID)
);

comment on column XXL_JOB_LOGGLUE.GLUE_SOURCE
    is 'GLUE源代码';
comment on column XXL_JOB_LOGGLUE.GLUE_REMARK
    is 'GLUE备注';
comment on column XXL_JOB_LOGGLUE.JOB_ID
    is '任务，主键ID';
comment on column XXL_JOB_LOGGLUE.GLUE_TYPE
    is 'GLUE类型';

--drop table XXL_JOB_REGISTRY;
CREATE TABLE XXL_JOB_REGISTRY
(
    ID             INT                NOT NULL,
    REGISTRY_GROUP VARCHAR(50)        NOT NULL,
    REGISTRY_KEY   VARCHAR(255)       NOT NULL,
    REGISTRY_VALUE VARCHAR(255)       NOT NULL,
    UPDATE_TIME    TIMESTAMP,
    PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX IDX_JOB_REGISTRY_GROUP_KEY_VALUE ON XXL_JOB_REGISTRY (REGISTRY_GROUP,REGISTRY_KEY,REGISTRY_VALUE);


--drop table XXL_JOB_GROUP;
create table XXL_JOB_GROUP
(
    ID            INT NOT NULL,
    APP_NAME      VARCHAR(64) not null,
    TITLE         VARCHAR(12) not null,
    ADDRESS_TYPE  TINYINT DEFAULT 0 NOT NULL,
    ADDRESS_LIST  TEXT,
    UPDATE_TIME   TIMESTAMP,
    PRIMARY KEY (ID)
);
comment on column XXL_JOB_GROUP.APP_NAME
    is '执行器AppName';
comment on column XXL_JOB_GROUP.TITLE
    is '执行器名称';
comment on column XXL_JOB_GROUP.ADDRESS_TYPE
    is '执行器地址类型：0=自动注册、1=手动录入';
comment on column XXL_JOB_GROUP.ADDRESS_LIST
    is '执行器地址列表，多地址逗号分隔';

--drop table XXL_JOB_USER;
create table XXL_JOB_USER
(
    ID         INT                NOT NULL,
    USERNAME   VARCHAR(50)        NOT NULL,
    PASSWORD   VARCHAR(50)        NOT NULL,
    ROLE       TINYINT            NOT NULL,
    PERMISSION VARCHAR(255),
    PRIMARY KEY (ID)
);

comment on column XXL_JOB_USER.USERNAME
    is '账号';
comment on column XXL_JOB_USER.PASSWORD
    is '密码';
comment on column XXL_JOB_USER.ROLE
    is '角色：0-普通用户、1-管理员';
comment on column XXL_JOB_USER.PERMISSION
    is '权限：执行器ID列表，多个逗号分割';

CREATE UNIQUE INDEX I_USERNAME ON XXL_JOB_USER(USERNAME);

--drop table XXL_JOB_LOCK;
create table XXL_JOB_LOCK(
    LOCK_NAME VARCHAR(50) NOT NULL,
    PRIMARY KEY (LOCK_NAME)
) ;

comment on column XXL_JOB_LOCK.LOCK_NAME
    is '锁名称';


-- Create sequence
create sequence XXL_JOB_GROUP_ID
    minvalue 1
    maxvalue 999999999999
    start with 2
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_INFO_ID
    minvalue 1
    maxvalue 999999999999
    start with 2
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_LOGGLUE_ID
    minvalue 1
    maxvalue 999999999999
    start with 1
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_LOG_ID
    minvalue 1
    maxvalue 999999999999
    start with 1
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_LOG_REPORT_ID
    minvalue 1
    maxvalue 999999999999
    start with 2
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_REGISTRY_ID
    minvalue 1
    maxvalue 999999999999
    start with 1
    increment by 1
    cache 20
    cycle;

-- Create sequence
create sequence XXL_JOB_USER_ID
    minvalue 1
    maxvalue 999999999999
    start with 2
    increment by 1
    cache 20
    cycle;

-- 插入 XXL_JOB_GROUP 数据
INSERT INTO XXL_JOB_GROUP (ID, APP_NAME, TITLE, ADDRESS_TYPE, ADDRESS_LIST, UPDATE_TIME)
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, TO_TIMESTAMP('2018-11-03 22:21:31', 'YYYY-MM-DD HH24:MI:SS'));

-- 插入 XXL_JOB_INFO 数据
INSERT INTO XXL_JOB_INFO (ID, JOB_GROUP, JOB_DESC, ADD_TIME, UPDATE_TIME, AUTHOR, ALARM_EMAIL,
                          SCHEDULE_TYPE, SCHEDULE_CONF, MISFIRE_STRATEGY, EXECUTOR_ROUTE_STRATEGY,
                          EXECUTOR_HANDLER, EXECUTOR_PARAM, EXECUTOR_BLOCK_STRATEGY, EXECUTOR_TIMEOUT,
                          EXECUTOR_FAIL_RETRY_COUNT, GLUE_TYPE, GLUE_SOURCE, GLUE_REMARK, GLUE_UPDATETIME,
                          CHILD_JOBID)
VALUES (1, 1, '测试任务1',
        TO_TIMESTAMP('2018-11-03 22:21:31', 'YYYY-MM-DD HH24:MI:SS'),
        TO_TIMESTAMP('2018-11-03 22:21:31', 'YYYY-MM-DD HH24:MI:SS'),
        'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0,
        'BEAN', '', 'GLUE代码初始化',
        TO_TIMESTAMP('2018-11-03 22:21:31', 'YYYY-MM-DD HH24:MI:SS'),
        '');

-- 插入 XXL_JOB_USER 数据
INSERT INTO XXL_JOB_USER (ID, USERNAME, PASSWORD, ROLE, PERMISSION)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);

-- 插入 XXL_JOB_LOCK 数据
INSERT INTO XXL_JOB_LOCK (LOCK_NAME) VALUES ('schedule_lock');