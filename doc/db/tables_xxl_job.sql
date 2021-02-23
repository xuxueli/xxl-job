#
# XXL-JOB v2.3.0
# Copyright (c) 2015-present, xuxueli.

CREATE TABLE xxl_job_info (
  id number(11) NOT NULL,
  job_group number(11) NOT NULL,
  job_desc varchar2(255) NOT NULL,
  add_time date DEFAULT NULL,
  update_time date DEFAULT NULL,
  author varchar2(64) DEFAULT NULL,
  alarm_email varchar2(255) DEFAULT NULL,
  schedule_type varchar2(50) DEFAULT 'NONE',
  schedule_conf varchar2(128) DEFAULT NULL,
  misfire_strategy varchar2(50) DEFAULT 'DO_NOTHING',
  executor_route_strategy varchar2(50) DEFAULT NULL,
  executor_handler varchar2(255) DEFAULT NULL,
  executor_param varchar2(512) DEFAULT NULL,
  executor_block_strategy varchar2(50) DEFAULT NULL,
  executor_timeout number(11) DEFAULT '0',
  executor_fail_retry_count number(11) DEFAULT '0',
  glue_type varchar2(50) NOT NULL,
  glue_source clob,
  glue_remark varchar2(128) DEFAULT NULL,
  glue_updatetime date DEFAULT NULL,
  child_jobid varchar2(255) DEFAULT NULL,
  trigger_status number(4) DEFAULT '0',
  trigger_last_time number(13) DEFAULT '0',
  trigger_next_time number(13) DEFAULT '0',
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_info is 'Job任务信息表';
-- Add comments to the columns
comment on column xxl_job_info.job_group is '执行器主键ID';
comment on column xxl_job_info.job_desc is '执行器描述信息';
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
comment on column xxl_job_info.glue_type is 'GLUE类型';
comment on column xxl_job_info.glue_source is 'GLUE源代码';
comment on column xxl_job_info.glue_remark is 'GLUE备注';
comment on column xxl_job_info.glue_updatetime is 'GLUE更新时间';
comment on column xxl_job_info.child_jobid is '子任务ID，多个逗号分隔';
comment on column xxl_job_info.trigger_status is '调度状态：0-停止，1-运行';
comment on column xxl_job_info.trigger_last_time is '上次调度时间';
comment on column xxl_job_info.trigger_next_time is '下次调度时间';
--Sequence
CREATE SEQUENCE seq_xxl_job_info
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_info
  before insert on xxl_job_info
  for each row
  begin
    select seq_xxl_job_info.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_log (
  id number(20) NOT NULL,
  job_group number(11) NOT NULL,
  job_id number(11) NOT NULL,
  executor_address varchar2(255) DEFAULT NULL,
  executor_handler varchar2(255) DEFAULT NULL,
  executor_param varchar2(512) DEFAULT NULL,
  executor_sharding_param varchar2(20) DEFAULT NULL,
  executor_fail_retry_count number(11) DEFAULT '0',
  trigger_time date DEFAULT NULL,
  trigger_code number(11) NOT NULL,
  trigger_msg clob,
  handle_time date DEFAULT NULL,
  handle_code number(11) NOT NULL,
  handle_msg clob,
  alarm_status number(4) DEFAULT '0',
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_log is 'Job任务调度日志表';
-- Add comments to the columns
comment on column xxl_job_log.job_group is '执行器主键ID';
comment on column xxl_job_log.job_id is '任务，主键ID';
comment on column xxl_job_log.executor_address is '执行器地址，本次执行的地址';
comment on column xxl_job_log.executor_handler is '执行器任务handler';
comment on column xxl_job_log.executor_param is '执行器任务参数';
comment on column xxl_job_log.executor_sharding_param is '执行器任务分片参数，格式如 1/2';
comment on column xxl_job_log.executor_fail_retry_count is '失败重试次数';
comment on column xxl_job_log.trigger_time is '调度-时间';
comment on column xxl_job_log.trigger_code is '调度-结果';
comment on column xxl_job_log.trigger_msg is '调度-日志';
comment on column xxl_job_log.handle_time is '执行-时间';
comment on column xxl_job_log.handle_code is '执行-结果';
comment on column xxl_job_log.handle_msg is '执行-日志';
comment on column xxl_job_log.alarm_status is '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';
-- Create indexes
CREATE INDEX idx_job_log_trigger_time ON xxl_job_log(trigger_time);
CREATE INDEX idx_job_log_handle_code ON xxl_job_log(handle_code);
CREATE INDEX idx_job_log_criteria ON xxl_job_log(job_group, job_id, handle_code, handle_time);
--Sequence
CREATE SEQUENCE seq_xxl_job_log
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_log
  before insert on xxl_job_log
  for each row
  begin
    select seq_xxl_job_log.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_log_report (
  id number(11) NOT NULL,
  trigger_day date DEFAULT NULL,
  running_count number(11) DEFAULT '0',
  suc_count number(11) DEFAULT '0',
  fail_count number(11) DEFAULT '0',
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_log_report is 'Job任务调度报表';
-- Add comments to the columns
comment on column xxl_job_log_report.trigger_day is '调度-时间';
comment on column xxl_job_log_report.running_count is '运行中-日志数量';
comment on column xxl_job_log_report.suc_count is '执行成功-日志数量';
comment on column xxl_job_log_report.fail_count is '执行失败-日志数量';
-- Create indexes
CREATE UNIQUE INDEX ux_job_report_trigger_day ON xxl_job_log_report(trigger_day);
--Sequence
CREATE SEQUENCE seq_xxl_job_log_report
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_log_report
  before insert on xxl_job_log_report
  for each row
  begin
    select seq_xxl_job_log_report.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_logglue (
  id number(11) NOT NULL,
  job_id number(11) NOT NULL,
  glue_type varchar2(50) DEFAULT NULL,
  glue_source clob,
  glue_remark varchar2(128) NOT NULL,
  add_time date DEFAULT NULL,
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_logglue is 'Job任务GLUE信息';
-- Add comments to the columns
comment on column xxl_job_logglue.job_id is '任务，主键ID';
comment on column xxl_job_logglue.glue_type is 'GLUE类型';
comment on column xxl_job_logglue.glue_source is 'GLUE源代码';
comment on column xxl_job_logglue.glue_remark is 'GLUE备注';
--Sequence
CREATE SEQUENCE seq_xxl_job_logglue
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_logglue
  before insert on xxl_job_logglue
  for each row
  begin
    select seq_xxl_job_logglue.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_registry (
  id number(11) NOT NULL,
  registry_group varchar2(50) NOT NULL,
  registry_key varchar2(255) NOT NULL,
  registry_value varchar2(255) NOT NULL,
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_registry is 'Job任务注册';
-- Add comments to the columns
comment on column xxl_job_registry.registry_group is '分组';
comment on column xxl_job_registry.registry_key is '注册key';
comment on column xxl_job_registry.registry_value is '注册value';
-- Create indexes
CREATE INDEX idx_job_registry_igkv ON xxl_job_registry(registry_group,registry_key,registry_value);
--Sequence
CREATE SEQUENCE seq_xxl_job_registry
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_registry
  before insert on xxl_job_registry
  for each row
  begin
    select seq_xxl_job_registry.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_group (
  id number(11) NOT NULL,
  app_name varchar2(64) NOT NULL,
  title varchar2(12) NOT NULL,
  address_type number(4) DEFAULT '0',
  address_list clob,
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_group is 'Job任务执行器信息';
-- Add comments to the columns
comment on column xxl_job_group.app_name is '执行器AppName';
comment on column xxl_job_group.title is '执行器名称';
comment on column xxl_job_group.address_type is '执行器地址类型：0=自动注册、1=手动录入';
comment on column xxl_job_group.address_list is '执行器地址列表，多地址逗号分隔';
--Sequence
CREATE SEQUENCE seq_xxl_job_group
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_group
  before insert on xxl_job_group
  for each row
  begin
    select seq_xxl_job_group.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_user (
  id number(11) NOT NULL,
  username varchar2(50) NOT NULL,
  password varchar2(50) NOT NULL,
  role number(4) NOT NULL,
  permission varchar2(255) DEFAULT NULL,
  PRIMARY KEY (id)
);
-- Add comments to the table
comment on table xxl_job_user is 'Job任务管理的用户与权限';
-- Add comments to the columns
comment on column xxl_job_user.username is '账号';
comment on column xxl_job_user.password is '密码';
comment on column xxl_job_user.role is '角色：0-普通用户、1-管理员';
comment on column xxl_job_user.permission is '权限：执行器ID列表，多个逗号分割';
-- Create indexes
CREATE UNIQUE INDEX ux_job_user_username ON xxl_job_user(username);
--Sequence
CREATE SEQUENCE seq_xxl_job_user
  INCREMENT BY 1
  START WITH 1
  NOMAXVALUE
  NOCYCLE
  cache 20;
-- Create auto_increment trigger
create or replace trigger trg_xxl_job_user
  before insert on xxl_job_user
  for each row
  begin
    select seq_xxl_job_user.nextval into :new.id from dual;
  end;
/

CREATE TABLE xxl_job_lock (
  lock_name varchar2(50) NOT NULL,
  PRIMARY KEY (lock_name)
);
-- Add comments to the table
comment on table xxl_job_lock is 'Job任务锁';
-- Add comments to the columns
comment on column xxl_job_lock.lock_name is '锁名称';

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time) VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, to_date('2018-11-03 22:21:31','yyyy-mm-dd hh24:mi:ss') );
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid) VALUES (1, 1, '测试任务1', to_date('2018-11-03 22:21:31','yyyy-mm-dd hh24:mi:ss'), to_date('2018-11-03 22:21:31','yyyy-mm-dd hh24:mi:ss'), 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', to_date('2018-11-03 22:21:31','yyyy-mm-dd hh24:mi:ss'), '');
INSERT INTO xxl_job_user(id, username, password, role, permission) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');

commit;

