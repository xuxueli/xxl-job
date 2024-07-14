use system;
create database xg_job CHARACTER SET utf8;
-- SYSDBA.UNDO_LOG definition
-- SYSDBA.UNDO_LOG definition
use xg_job;


create table "SYSDBA"."UNDO_LOG"(
"branch_id" bigint not null comment 'branch transaction id',
"xid" varchar(100) not null comment 'global transaction id',
"context" varchar(128) not null comment 'undo_log context,such as serialization',
"rollback_info" blob not null comment 'rollback info',
"log_status" integer not null comment '0:normal status,1:defense status',
"log_created" datetime not null comment 'create datetime',
"log_modified" datetime not null comment 'modify datetime'
) comment 'AT transaction mode undo table';


-- SYSDBA.XXL_JOB_GROUP definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_GROUP"(
"id" integer not null,
"app_name" varchar(64) not null comment '执行器AppName',
"title" varchar(12) not null comment '执行器名称',
"address_type" tinyint not null default 0 comment '执行器地址类型：0=自动注册、1=手动录入',
"address_list" clob comment '执行器地址列表，多地址逗号分隔',
"update_time" datetime
);

-- SYSDBA.XXL_JOB_INFO definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_INFO"(
"id" integer not null,
"job_group" integer not null comment '执行器主键ID',
"job_desc" varchar(255) not null,
"add_time" datetime,
"update_time" datetime,
"author" varchar(64) comment '作者',
"alarm_email" varchar(255) comment '报警邮件',
"schedule_type" varchar(50) not null default 'NONE' comment '调度类型',
"schedule_conf" varchar(128) comment '调度配置，值含义取决于调度类型',
"misfire_strategy" varchar(50) not null default 'DO_NOTHING' comment '调度过期策略',
"executor_route_strategy" varchar(50) comment '执行器路由策略',
"executor_handler" varchar(255) comment '执行器任务handler',
"executor_param" varchar comment '执行器任务参数',
"executor_block_strategy" varchar(50) comment '阻塞处理策略',
"executor_timeout" integer not null default 0 comment '任务执行超时时间，单位秒',
"executor_fail_retry_count" integer not null default 0 comment '失败重试次数',
"glue_type" varchar(50) not null comment 'GLUE类型',
"glue_source" clob comment 'GLUE源代码',
"glue_remark" varchar(128) comment 'GLUE备注',
"glue_updatetime" datetime comment 'GLUE更新时间',
"child_jobid" varchar(255) comment '子任务ID，多个逗号分隔',
"trigger_status" tinyint not null default 0 comment '调度状态：0-停止，1-运行',
"trigger_last_time" bigint not null default 0 comment '上次调度时间',
"trigger_next_time" bigint not null default 0 comment '下次调度时间'
);

-- SYSDBA.XXL_JOB_LOCK definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_LOCK"(
"lock_name" varchar(50) not null comment '锁名称'
);


-- SYSDBA.XXL_JOB_LOG definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_LOG"(
"id" bigint not null,
"job_group" integer not null comment '执行器主键ID',
"job_id" integer not null comment '任务，主键ID',
"executor_address" varchar(255) comment '执行器地址，本次执行的地址',
"executor_handler" varchar(255) comment '执行器任务handler',
"executor_param" varchar(512) comment '执行器任务参数',
"executor_sharding_param" varchar(20) comment '执行器任务分片参数，格式如 1/2',
"executor_fail_retry_count" integer not null default 0 comment '失败重试次数',
"trigger_time" datetime comment '调度-时间',
"trigger_code" integer not null comment '调度-结果',
"trigger_msg" clob comment '调度-日志',
"handle_time" datetime comment '执行-时间',
"handle_code" integer not null comment '执行-状态',
"handle_msg" clob comment '执行-日志',
"alarm_status" tinyint not null default 0 comment '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败'
);



-- SYSDBA.XXL_JOB_LOGGLUE definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_LOGGLUE"(
"id" integer not null,
"job_id" integer not null comment '任务，主键ID',
"glue_type" varchar(50) comment 'GLUE类型',
"glue_source" clob comment 'GLUE源代码',
"glue_remark" varchar(128) not null comment 'GLUE备注',
"add_time" datetime,
"update_time" datetime
);


-- SYSDBA.XXL_JOB_LOG_REPORT definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_LOG_REPORT"(
"id" integer not null,
"trigger_day" datetime comment '调度-时间',
"running_count" integer not null default 0 comment '运行中-日志数量',
"suc_count" integer not null default 0 comment '执行成功-日志数量',
"fail_count" integer not null default 0 comment '执行失败-日志数量',
"update_time" datetime
);


-- SYSDBA.XXL_JOB_REGISTRY definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_REGISTRY"(
"id" integer not null,
"registry_group" varchar(50) not null,
"registry_key" varchar(255) not null,
"registry_value" varchar(255) not null,
"update_time" datetime
);


-- SYSDBA.XXL_JOB_USER definition

-- Create Table --
create table "SYSDBA"."XXL_JOB_USER"(
"id" integer not null,
"username" varchar(50) not null comment '账号',
"password" varchar(50) not null comment '密码',
"role" tinyint not null comment '角色：0-普通用户、1-管理员',
"permission" varchar(255) comment '权限：执行器ID列表，多个逗号分割'
);



alter table "SYSDBA"."XXL_JOB_GROUP" alter column "id" INTEGER identity(2,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_INFO" alter column "id" INTEGER identity(3,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_LOG_REPORT" alter column "id" INTEGER identity(7,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_LOG" alter column "id" BIGINT identity(17,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_LOGGLUE" alter column "id" INTEGER identity(1,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_REGISTRY" alter column "id" INTEGER identity(3,1) CASCADE;
alter table "SYSDBA"."XXL_JOB_USER" alter column "id" INTEGER identity(2,1) CASCADE;

alter table "SYSDBA"."XXL_JOB_LOCK" add constraint "PRIMARY" primary key("lock_name");

alter table "SYSDBA"."UNDO_LOG" add constraint "UX_UNDO_LOG" unique("xid","branch_id");
alter table "SYSDBA"."XXL_JOB_LOG_REPORT" add constraint "I_TRIGGER_DAY" unique("trigger_day");
alter table "SYSDBA"."XXL_JOB_USER" add constraint "I_USERNAME" unique("username");



create index "I_HANDLE_CODE" on "SYSDBA"."XXL_JOB_LOG"("handle_code") indextype is btree;
create index "I_TRIGGER_TIME" on "SYSDBA"."XXL_JOB_LOG"("trigger_time") indextype is btree;
create index "I_G_K_V" on "SYSDBA"."XXL_JOB_REGISTRY"("registry_group","registry_key","registry_value") indextype is btree;

INSERT INTO `XXL_JOB_USER`(`id`, `username`, `password`, `role`, `permission`) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO `XXL_JOB_LOCK` ( `lock_name`) VALUES ( 'schedule_lock');
