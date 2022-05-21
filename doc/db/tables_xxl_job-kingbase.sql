-- 需要单独创建数据库和指定数据库后再创建表
CREATE DATABASE xxl_job OWNER system ;

CREATE TABLE xxl_job_info (
  id SERIAL,
  job_group int NOT NULL ,
  job_desc varchar(255) NOT NULL,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  author varchar(64) DEFAULT NULL ,
  alarm_email varchar(255) DEFAULT NULL ,
  schedule_type varchar(50) NOT NULL DEFAULT 'NONE' ,
  schedule_conf varchar(128) DEFAULT NULL ,
  misfire_strategy varchar(50) NOT NULL DEFAULT 'DO_NOTHING' ,
  executor_route_strategy varchar(50) DEFAULT NULL ,
  executor_handler varchar(255) DEFAULT NULL ,
  executor_param varchar(512) DEFAULT NULL ,
  executor_block_strategy varchar(50) DEFAULT NULL ,
  executor_timeout int NOT NULL DEFAULT '0' ,
  executor_fail_retry_count int NOT NULL DEFAULT '0' ,
  glue_type varchar(50) NOT NULL ,
  glue_source CLOB ,
  glue_remark varchar(128) DEFAULT NULL ,
  glue_updatetime datetime DEFAULT NULL ,
  child_job_id varchar(255) DEFAULT NULL ,
  trigger_status NUMBER(3,0) NOT NULL DEFAULT '0',
  trigger_last_time number NOT NULL DEFAULT '0',
  trigger_next_time number NOT NULL DEFAULT '0',
  PRIMARY KEY (id)
) ;

COMMENT ON COLUMN xxl_job_info.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_info.author IS '作者';
COMMENT ON COLUMN xxl_job_info.alarm_email IS '报警邮件';
COMMENT ON COLUMN xxl_job_info.schedule_type IS '调度类型';
COMMENT ON COLUMN xxl_job_info.schedule_conf IS '调度配置，值含义取决于调度类型';
COMMENT ON COLUMN xxl_job_info.misfire_strategy IS '调度过期策略';
COMMENT ON COLUMN xxl_job_info.executor_route_strategy IS '执行器路由策略';
COMMENT ON COLUMN xxl_job_info.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_info.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_info.executor_block_strategy IS '阻塞处理策略';
COMMENT ON COLUMN xxl_job_info.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN xxl_job_info.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_info.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_info.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_info.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_info.glue_updatetime IS 'GLUE更新时间';
COMMENT ON COLUMN xxl_job_info.child_job_id IS '子任务ID，多个逗号分隔';
COMMENT ON COLUMN xxl_job_info.trigger_status IS '调度状态：0-停止，1-运行';
COMMENT ON COLUMN xxl_job_info.trigger_last_time IS '上次调度时间';
COMMENT ON COLUMN xxl_job_info.trigger_next_time IS '下次调度时间';



CREATE TABLE xxl_job_log (
  id SERIAL,
  job_group int NOT NULL ,
  job_id int NOT NULL ,
  executor_address varchar(255) DEFAULT NULL ,
  executor_handler varchar(255) DEFAULT NULL ,
  executor_param varchar(512) DEFAULT NULL ,
  executor_sharding_param varchar(20) DEFAULT NULL ,
  executor_fail_retry_count int NOT NULL DEFAULT '0' ,
  trigger_time datetime DEFAULT NULL,
  trigger_code int NOT NULL ,
  trigger_msg clob ,
  handle_time datetime DEFAULT NULL ,
  handle_code int NOT NULL ,
  handle_msg CLOB ,
  alarm_status NUMBER(3,0) NOT NULL DEFAULT '0' ,
  PRIMARY KEY (id)
) ;

create index I_trigger_time on xxl_job_log(trigger_time);
create index I_handle_code on xxl_job_log(handle_code);


COMMENT ON COLUMN xxl_job_log.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_log.job_id IS '任务，主键ID';
COMMENT ON COLUMN xxl_job_log.executor_address IS '执行器地址，本次执行的地址';
COMMENT ON COLUMN xxl_job_log.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_log.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_log.executor_sharding_param IS '执行器任务分片参数，格式如 1/2';
COMMENT ON COLUMN xxl_job_log.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_log.trigger_time IS '调度-时间';
COMMENT ON COLUMN xxl_job_log.trigger_code IS '调度-结果';
COMMENT ON COLUMN xxl_job_log.trigger_msg IS '调度-日志';
COMMENT ON COLUMN xxl_job_log.handle_time IS '执行-时间';
COMMENT ON COLUMN xxl_job_log.handle_code IS '执行-状态';
COMMENT ON COLUMN xxl_job_log.handle_msg IS '执行-日志';
COMMENT ON COLUMN xxl_job_log.alarm_status IS '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';




CREATE TABLE xxl_job_log_report (
  id SERIAL,
  trigger_day datetime DEFAULT NULL ,
  running_count int NOT NULL DEFAULT '0',
  suc_count int NOT NULL DEFAULT '0',
  fail_count int NOT NULL DEFAULT '0',
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ;
ALTER TABLE xxl_job_log_report
    ADD CONSTRAINT i_trigger_day UNIQUE (trigger_day);

COMMENT ON COLUMN xxl_job_log_report.trigger_day IS '调度-时间';
COMMENT ON COLUMN xxl_job_log_report.running_count IS '运行中-日志数量';
COMMENT ON COLUMN xxl_job_log_report.suc_count IS '执行成功-日志数量';
COMMENT ON COLUMN xxl_job_log_report.fail_count IS '执行失败-日志数量';


CREATE TABLE xxl_job_logglue (
  id SERIAL,
  job_id int NOT NULL ,
  glue_type varchar(50) DEFAULT NULL,
  glue_source clob ,
  glue_remark varchar(128) NOT NULL ,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ;

COMMENT ON COLUMN xxl_job_logglue.job_id IS '任务，主键ID';
COMMENT ON COLUMN xxl_job_logglue.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_logglue.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_logglue.glue_remark IS 'GLUE备注';



CREATE TABLE xxl_job_registry (
  id SERIAL,
  registry_group varchar(50) NOT NULL,
  registry_key varchar(255) NOT NULL,
  registry_value varchar(255) NOT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ;
create index i_g_k_v on xxl_job_registry(registry_group,registry_key,registry_value);


CREATE TABLE xxl_job_group (
  id SERIAL,
  app_name varchar(64) NOT NULL,
  title varchar(12) NOT NULL ,
  address_type NUMBER(3,0) NOT NULL DEFAULT '0' ,
  address_list clob ,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ;
COMMENT ON COLUMN xxl_job_group.app_name IS '执行器AppName';
COMMENT ON COLUMN xxl_job_group.title IS '执行器名称';
COMMENT ON COLUMN xxl_job_group.address_type IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN xxl_job_group.address_list IS '执行器地址列表，多地址逗号分隔';




CREATE TABLE xxl_job_user (
  id SERIAL,
  username varchar(50) NOT NULL,
  password varchar(50) NOT NULL ,
  role NUMBER(3,0) NOT NULL ,
  permission varchar(255) DEFAULT NULL ,
  PRIMARY KEY (id)
) ;

ALTER TABLE xxl_job_user
    ADD CONSTRAINT i_username UNIQUE (username);
COMMENT ON COLUMN xxl_job_user.username IS '账号';
COMMENT ON COLUMN xxl_job_user.password IS '密码';
COMMENT ON COLUMN xxl_job_user.role IS '角色：0-普通用户、1-管理员';
COMMENT ON COLUMN xxl_job_user.permission IS '权限：执行器ID列表，多个逗号分割';


CREATE TABLE xxl_job_lock (
  lock_name varchar(50) NOT NULL,
  PRIMARY KEY (lock_name)
) ;

COMMENT ON COLUMN xxl_job_lock.lock_name IS '锁名称';

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time)
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author,
                           alarm_email, schedule_type, schedule_conf, misfire_strategy,
                           executor_route_strategy, executor_handler, executor_param,
                           executor_block_strategy, executor_timeout, executor_fail_retry_count,
                           glue_type, glue_source, glue_remark, glue_updatetime, child_job_id)
VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');

INSERT INTO xxl_job_user(id, username, password, role, permission)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');



