--
-- XXL-JOB v2.4.0
-- Copyright (c) 2015-present, xuxueli.



CREATE TABLE xxl_job_info
(
    id                        bigint auto_increment      NOT NULL ,
    job_group                 integer      NOT NULL ,
    job_desc                  varchar(255) NOT NULL,
    add_time                  timestamp              ,
    update_time               timestamp              ,
    author                    varchar(64)            ,
    alarm_email               varchar(255)           ,
    schedule_type             varchar(50)  NOT NULL DEFAULT 'NONE' ,
    schedule_conf             varchar(128)           ,
    misfire_strategy          varchar(50)  NOT NULL DEFAULT 'DO_NOTHING' ,
    executor_route_strategy   varchar(50)            ,
    executor_handler          varchar(255)           ,
    executor_param            varchar(512)           ,
    executor_block_strategy   varchar(50)            ,
    executor_timeout          integer      NOT NULL DEFAULT 0 ,
    executor_fail_retry_count integer      NOT NULL DEFAULT 0 ,
    glue_type                 varchar(50)  NOT NULL ,
    glue_source               text ,
    glue_remark               varchar(128)           ,
    glue_updatetime           timestamp               ,
    child_jobid               varchar(255)           ,
    trigger_status            int4   NOT NULL DEFAULT 0 ,
    trigger_last_time         bigint   NOT NULL DEFAULT 0 ,
    trigger_next_time         bigint   NOT NULL DEFAULT 0 ,
    PRIMARY KEY (id)
)
;


comment on column xxl_job_info.job_group is '执行器主键id';
comment on column xxl_job_info.author is '作者';
comment on column xxl_job_info.alarm_email is '报警邮件';
comment on column xxl_job_info.schedule_type is '调度类型';
comment on column xxl_job_info.schedule_conf is '调度配置，值含义取决于调度类型';
comment on column xxl_job_info.misfire_strategy is '调度过期策略';
comment on column xxl_job_info.executor_route_strategy is '执行器路由策略';
comment on column xxl_job_info.executor_handler is '执行器任务handler';
comment on column xxl_job_info.executor_param is '执行器任务参数';
comment on column xxl_job_info.executor_block_strategy is '阻塞处理策略';
comment on column xxl_job_info.executor_timeout is '任务执行超时时间，单位秒';
comment on column xxl_job_info.executor_fail_retry_count is '失败重试次数';
comment on column xxl_job_info.glue_type is 'glue类型';
comment on column xxl_job_info.glue_source is 'glue源代码';
comment on column xxl_job_info.glue_updatetime is 'glue更新时间';
comment on column xxl_job_info.child_jobid is '子任务id，多个逗号分隔';
comment on column xxl_job_info.trigger_status is '调度状态：0-停止，1-运行';
comment on column xxl_job_info.trigger_last_time is '上次调度时间';
comment on column xxl_job_info.trigger_next_time is '下次调度时间';


CREATE TABLE xxl_job_log
(
    id                        bigint auto_increment NOT NULL ,
    job_group                 integer    NOT NULL ,
    job_id                    integer    NOT NULL ,
    executor_address          varchar(255)         ,
    executor_handler          varchar(255)         ,
    executor_param            varchar(512)         ,
    executor_sharding_param   varchar(20)          ,
    executor_fail_retry_count integer    NOT NULL DEFAULT 0 ,
    trigger_time              timestamp             ,
    trigger_code              integer    NOT NULL ,
    trigger_msg               text ,
    handle_time               timestamp             ,
    handle_code               integer    NOT NULL ,
    handle_msg                text ,
    alarm_status              int4 NOT NULL DEFAULT 0 ,
    PRIMARY KEY (id)
)
;


create index idx_xxl_job_log_trigger_time on xxl_job_log (trigger_time)
;

create index idx_xxl_job_log_handle_code on xxl_job_log (handle_code)
;


comment on column xxl_job_log.job_group is '执行器主键id';
comment on column xxl_job_log.job_id is '任务，主键id';
comment on column xxl_job_log.executor_address is '执行器地址，本次执行的地址';
comment on column xxl_job_log.executor_handler is '执行器任务handler';
comment on column xxl_job_log.executor_param is '执行器任务参数';
comment on column xxl_job_log.executor_sharding_param is '执行器任务分片参数，格式如 1/2';
comment on column xxl_job_log.executor_fail_retry_count is '失败重试次数';
comment on column xxl_job_log.trigger_time is '调度-时间';
comment on column xxl_job_log.trigger_code is '调度-结果';
comment on column xxl_job_log.trigger_msg is '调度-日志';
comment on column xxl_job_log.handle_time is '执行-时间';
comment on column xxl_job_log.handle_code is '执行-状态';
comment on column xxl_job_log.handle_msg is '执行-日志';
comment on column xxl_job_log.alarm_status is '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';


CREATE TABLE xxl_job_log_report
(
    id            bigint auto_increment NOT NULL,
    trigger_day   timestamp          ,
    running_count integer NOT NULL DEFAULT 0 ,
    suc_count     integer NOT NULL DEFAULT 0 ,
    fail_count    integer NOT NULL DEFAULT 0 ,
    update_time   timestamp         ,
    PRIMARY KEY (id)
)
;


create index idx_xxl_job_log_report_trigger_day on xxl_job_log_report (trigger_day)
;


comment on column xxl_job_log_report.trigger_day is '调度-时间';
comment on column xxl_job_log_report.running_count is '运行中-日志数量';
comment on column xxl_job_log_report.suc_count is '执行成功-日志数量';
comment on column xxl_job_log_report.fail_count is '执行失败-日志数量';


CREATE TABLE xxl_job_logglue
(
    id          bigint  auto_increment    NOT NULL,
    job_id      integer      NOT NULL ,
    glue_type   varchar(50)  ,
    glue_source text ,
    glue_remark varchar(128) NOT NULL ,
    add_time    timestamp    ,
    update_time timestamp    ,
    PRIMARY KEY (id)
)
;


comment on column xxl_job_logglue.job_id is '任务，主键id';
comment on column xxl_job_logglue.glue_type is 'glue类型';
comment on column xxl_job_logglue.glue_source is 'glue源代码';
comment on column xxl_job_logglue.glue_remark is 'glue备注';


CREATE TABLE xxl_job_registry
(
    id             bigint   auto_increment   NOT NULL ,
    registry_group varchar(50)  NOT NULL,
    registry_key   varchar(255) NOT NULL,
    registry_value varchar(255) NOT NULL,
    update_time    timestamp ,
    PRIMARY KEY (id)
)
;


create index idx_xxl_job_registry_g_k_v
    on xxl_job_registry (registry_group, registry_key, registry_value)
;

CREATE TABLE xxl_job_group
(
    id           bigint  auto_increment   NOT NULL ,
    app_name     varchar(64) NOT NULL ,
    title        varchar(64) NOT NULL ,
    address_type int4  NOT NULL DEFAULT 0 ,
    address_list text ,
    update_time  timestamp             ,
    PRIMARY KEY (id)
)
;


comment on column xxl_job_group.app_name is '执行器appname';
comment on column xxl_job_group.title is '执行器名称';
comment on column xxl_job_group.address_type is '执行器地址类型：0=自动注册、1=手动录入';
comment on column xxl_job_group.address_list is '执行器地址列表，多地址逗号分隔';


CREATE TABLE xxl_job_user
(
    id         bigint  auto_increment   NOT NULL ,
    username   varchar(50) NOT NULL ,
    password   varchar(300) NOT NULL ,
    role       int4  NOT NULL ,
    permission varchar(255)  ,
    PRIMARY KEY (id)
)
;


create unique index idx_xxl_job_user_username on xxl_job_user (username)
;

comment on column xxl_job_user.username is '账号';
comment on column xxl_job_user.password is '密码';
comment on column xxl_job_user.role is '角色：0-普通用户、1-管理员';
comment on column xxl_job_user.permission is '权限：执行器id列表，多个逗号分割';


CREATE TABLE xxl_job_lock
(
    lock_name varchar(50) NOT NULL ,
    PRIMARY KEY (lock_name)
)
;

comment on column xxl_job_lock.lock_name is '锁名称';




INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time)
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31');
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email,
                         schedule_type, schedule_conf, misfire_strategy, executor_route_strategy,
                         executor_handler, executor_param, executor_block_strategy, executor_timeout,
                         executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime,
                         child_jobid)
VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        '2018-11-03 22:21:31', '');
INSERT INTO xxl_job_user(id, username, password, role, permission)
VALUES (1, 'admin', '$2a$10$rElzP.wCmjyjsVIIqoP4fe8u1qH3otIxiG4UhYs9A3Ivsrm1LrpOu', 1, NULL);
INSERT INTO xxl_job_lock (lock_name)
VALUES ('schedule_lock');
