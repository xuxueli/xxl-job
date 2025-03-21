--drop table XXL_JOB_GROUP;
create table XXL_JOB_GROUP
(
    ID            NUMBER not null,
    APP_NAME      VARCHAR2(150) not null,
    TITLE         VARCHAR2(50) not null,
    ADDRESS_TYPE  NUMBER not null,
    ADDRESS_LIST  CLOB,
--                text

    UPDATE_TIME   TIMESTAMP
);
comment on table XXL_JOB_GROUP
    is '执行器信息表';
comment on column XXL_JOB_GROUP.ID
    is 'id';
comment on column XXL_JOB_GROUP.APP_NAME
    is '执行器AppName';
comment on column XXL_JOB_GROUP.TITLE
    is '执行器名称';
comment on column XXL_JOB_GROUP.ADDRESS_TYPE
    is '执行器地址类型：0=自动注册、1=手动录入';
comment on column XXL_JOB_GROUP.ADDRESS_LIST
    is '执行器地址列表，多地址逗号分隔';
comment on column XXL_JOB_GROUP.UPDATE_TIME
    is '';
create index IDX_JOB_GROUP_APP_NAME on XXL_JOB_GROUP(APP_NAME) ;
alter table XXL_JOB_GROUP add constraint PK_XXL_JOB_GROUP primary key (ID)  ;

--drop table XXL_JOB_INFO;
create table XXL_JOB_INFO
(
    ID                        NUMBER not null,
    JOB_GROUP                 NUMBER not null,
    JOB_DESC                  VARCHAR2(500) not null,
    ADD_TIME                  TIMESTAMP,
    UPDATE_TIME               TIMESTAMP,
    AUTHOR                    VARCHAR2(500),
    ALARM_EMAIL               VARCHAR2(500),
    SCHEDULE_TYPE             VARCHAR2(50) NOT NULL DEFAULT 'NONE',
    SCHEDULE_CONF             VARCHAR2(128),
    MISFIRE_STRATEGY VARCHAR2(50) NOT NULL DEFAULT 'DO_NOTHING',
    EXECUTOR_ROUTE_STRATEGY   VARCHAR2(150),
    EXECUTOR_HANDLER          VARCHAR2(500),
    EXECUTOR_PARAM            VARCHAR2(1000),
    EXECUTOR_BLOCK_STRATEGY   VARCHAR2(150),
    EXECUTOR_TIMEOUT          NUMBER NOT NULL DEFAULT 0,
    EXECUTOR_FAIL_RETRY_COUNT NUMBER NOT NULL DEFAULT 0,
    GLUE_TYPE                 VARCHAR2(150) not null,
    GLUE_SOURCE               CLOB,
    GLUE_REMARK               VARCHAR2(128),
    GLUE_UPDATETIME           TIMESTAMP,
    CHILD_JOBID               VARCHAR2(500),
    TRIGGER_STATUS            NUMBER(1) NOT NULL DEFAULT 0 CHECK (TRIGGER_STATUS IN (0, 1)),
    TRIGGER_LAST_TIME         NUMBER not null,
    TRIGGER_NEXT_TIME         NUMBER not null
) ;
comment on table XXL_JOB_INFO
    is '调度信息表';
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
comment on column XXL_JOB_INFO.ID
    is '主键';
comment on column XXL_JOB_INFO.JOB_GROUP
    is '执行器主键ID';
comment on column XXL_JOB_INFO.JOB_DESC
    is '任务描述';
comment on column XXL_JOB_INFO.ADD_TIME
    is '添加时间';
comment on column XXL_JOB_INFO.UPDATE_TIME
    is '更新时间';
create index IDX_JOB_AUTHOR on XXL_JOB_INFO(AUTHOR) ;
create index IDX_JOB_EXECUTOR_HANDLER on XXL_JOB_INFO(EXECUTOR_HANDLER) ;
create index IDX_JOB_JOB_GROUP on XXL_JOB_INFO(JOB_GROUP) ;
create index IDX_JOB_TRIGGER_STATUS on XXL_JOB_INFO(TRIGGER_STATUS) ;
alter table XXL_JOB_INFO add constraint PK_XXL_JOB_INFO primary key (ID)  ;

--drop table XXL_JOB_LOCK;
create table XXL_JOB_LOCK(
                             LOCK_NAME VARCHAR2(150) not null
) ;
comment on table XXL_JOB_LOCK
    is '锁信息';
comment on column XXL_JOB_LOCK.LOCK_NAME
    is '锁名称';
alter table XXL_JOB_LOCK add constraint PK_XXL_JOB_LOCK primary key (LOCK_NAME)  ;

--drop table XXL_JOB_LOG;
create table XXL_JOB_LOG
(
    ID                        NUMBER not null,
    JOB_GROUP                 NUMBER not null,
    JOB_ID                    NUMBER not null,
    EXECUTOR_ADDRESS          VARCHAR2(500),
    EXECUTOR_HANDLER          VARCHAR2(500),
    EXECUTOR_PARAM            VARCHAR2(1000),
    EXECUTOR_SHARDING_PARAM   VARCHAR2(50),
    EXECUTOR_FAIL_RETRY_COUNT NUMBER not null,
    TRIGGER_TIME              TIMESTAMP,
    TRIGGER_CODE              NUMBER not null,
    TRIGGER_MSG               CLOB,
    HANDLE_TIME               TIMESTAMP,
    HANDLE_CODE               NUMBER not null,
    HANDLE_MSG                CLOB,
    ALARM_STATUS              NUMBER not null
) ;
comment on table XXL_JOB_LOG
    is '任务日志信息';
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
comment on column XXL_JOB_LOG.JOB_GROUP
    is '执行器主键ID';
comment on column XXL_JOB_LOG.ID
    is '主键ID';
create index IDX_JOB_LOG_TRIGGER_CODE on XXL_JOB_LOG(TRIGGER_CODE) ;
create index IDX_JOB_LOG_TRIGGER_TIME on XXL_JOB_LOG(TRIGGER_TIME) ;
alter table XXL_JOB_LOG add constraint PK_XXL_JOB_LOG primary key (ID)  ;
create index IDX_JOB_LOG_HANDLE_CODE on XXL_JOB_LOG(HANDLE_CODE) ;
create index IDX_JOB_LOG_HANDLE_TIME on XXL_JOB_LOG(HANDLE_TIME) ;
create index IDX_JOB_LOG_JOB_GROUP on XXL_JOB_LOG(JOB_GROUP) ;
create index IDX_JOB_LOG_JOB_ID on XXL_JOB_LOG(JOB_ID) ;
ALTER TABLE XXL_JOB_LOG MODIFY EXECUTOR_FAIL_RETRY_COUNT DEFAULT 0;
ALTER TABLE XXL_JOB_LOG MODIFY ALARM_STATUS DEFAULT 0;

--drop table XXL_JOB_LOGGLUE;
create table XXL_JOB_LOGGLUE
(
    ID          NUMBER not null,
    JOB_ID      NUMBER not null,
    GLUE_TYPE   VARCHAR2(150),
    GLUE_SOURCE CLOB,
    GLUE_REMARK VARCHAR2(256) not null,
    ADD_TIME    TIMESTAMP,
    UPDATE_TIME TIMESTAMP
) ;
comment on table XXL_JOB_LOGGLUE
    is '任务GLUE日志';
comment on column XXL_JOB_LOGGLUE.GLUE_SOURCE
    is 'GLUE源代码';
comment on column XXL_JOB_LOGGLUE.GLUE_REMARK
    is 'GLUE备注';
comment on column XXL_JOB_LOGGLUE.ADD_TIME
    is '添加时间';
comment on column XXL_JOB_LOGGLUE.UPDATE_TIME
    is '更新时间';
comment on column XXL_JOB_LOGGLUE.ID
    is '主键ID';
comment on column XXL_JOB_LOGGLUE.JOB_ID
    is '任务，主键ID';
comment on column XXL_JOB_LOGGLUE.GLUE_TYPE
    is 'GLUE类型';
create index IDX_JOB_LOGGLUE_JOB_ID on XXL_JOB_LOGGLUE(JOB_ID) ;
alter table XXL_JOB_LOGGLUE add constraint PK_XXL_JOB_LOGGLUE primary key (ID)  ;

--drop table XXL_JOB_LOG_REPORT;
create table XXL_JOB_LOG_REPORT
(
    ID            NUMBER(11) not null,
    TRIGGER_DAY   TIMESTAMP,
    RUNNING_COUNT NUMBER(11) not null,
    SUC_COUNT     NUMBER(11) not null,
    FAIL_COUNT    NUMBER(11) not null,
    UPDATE_TIME   TIMESTAMP
) ;
comment on table XXL_JOB_LOG_REPORT
    is '日志报表';
comment on column XXL_JOB_LOG_REPORT.ID
    is 'id';
comment on column XXL_JOB_LOG_REPORT.TRIGGER_DAY
    is '调度-时间';
comment on column XXL_JOB_LOG_REPORT.RUNNING_COUNT
    is '运行中-日志数量';
comment on column XXL_JOB_LOG_REPORT.SUC_COUNT
    is '执行成功-日志数量';
comment on column XXL_JOB_LOG_REPORT.FAIL_COUNT
    is '执行失败-日志数量';
comment on column XXL_JOB_LOG_REPORT.UPDATE_TIME
    is '更新时间';
alter table XXL_JOB_LOG_REPORT add constraint PK_XXL_JOB_LOG_REPORT primary key (ID)  ;

--drop table XXL_JOB_REGISTRY;
create table XXL_JOB_REGISTRY
(
    ID             NUMBER not null,
    REGISTRY_GROUP VARCHAR2(500) not null,
    REGISTRY_KEY   VARCHAR2(500) not null,
    REGISTRY_VALUE VARCHAR2(500) not null,
    UPDATE_TIME    TIMESTAMP
) ;
comment on table XXL_JOB_REGISTRY
    is '执行器注册表';
comment on column XXL_JOB_REGISTRY.ID
    is '主键ID';
comment on column XXL_JOB_REGISTRY.REGISTRY_GROUP
    is '注册组';
comment on column XXL_JOB_REGISTRY.REGISTRY_KEY
    is '注册key';
comment on column XXL_JOB_REGISTRY.REGISTRY_VALUE
    is '注册value';
comment on column XXL_JOB_REGISTRY.UPDATE_TIME
    is '修改时间';
create index IDX_JOB_REGISTRY_GROUP on XXL_JOB_REGISTRY(REGISTRY_GROUP) ;
create index IDX_JOB_REGISTRY_UPDATE_TIME on XXL_JOB_REGISTRY(UPDATE_TIME) ;
alter table XXL_JOB_REGISTRY add constraint PK_XXL_JOB_REGISTRY primary key (ID)  ;
ALTER TABLE xxl_job_registry
    ALTER COLUMN id SET DEFAULT nextval('XXL_JOB_REGISTRY_ID');


--drop table XXL_JOB_USER;
create table XXL_JOB_USER
(
    ID         NUMBER not null,
    USERNAME   VARCHAR2(150) not null,
    PASSWORD   VARCHAR2(150) not null,
    ROLE       NUMBER not null,
    PERMISSION VARCHAR2(500)
) ;
comment on table XXL_JOB_USER
    is '登录用户信息';
comment on column XXL_JOB_USER.ID
    is '主键ID';
comment on column XXL_JOB_USER.USERNAME
    is '账号';
comment on column XXL_JOB_USER.PASSWORD
    is '密码';
comment on column XXL_JOB_USER.ROLE
    is '角色：0-普通用户、1-管理员';
comment on column XXL_JOB_USER.PERMISSION
    is '权限：执行器ID列表，多个逗号分割';
alter table XXL_JOB_USER add constraint PK_XXL_JOB_USER primary key (ID)  ;



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

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list) VALUES (1, 'xxl-job-executor-sample', '示例执行器',  0, NULL);
INSERT INTO xxl_job_user(id, username, password, role, permission) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');
