--
-- XXL-JOB v2.3.1-SNAPSHOT
-- Copyright (c) 2015-present, xuxueli.

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_info (
  id number(10) NOT NULL,
  job_group number(10) NOT NULL ,
  job_desc varchar2(255) NOT NULL,
  add_time timestamp(0) DEFAULT NULL,
  update_time timestamp(0) DEFAULT NULL,
  author varchar2(64) DEFAULT NULL ,
  alarm_email varchar2(255) DEFAULT NULL ,
  schedule_type varchar2(50) DEFAULT 'NONE' NOT NULL ,
  schedule_conf varchar2(128) DEFAULT NULL ,
  misfire_strategy varchar2(50) DEFAULT 'DO_NOTHING' NOT NULL ,
  executor_route_strategy varchar2(50) DEFAULT NULL ,
  executor_handler varchar2(255) DEFAULT NULL ,
  executor_param varchar2(512) DEFAULT NULL ,
  executor_block_strategy varchar2(50) DEFAULT NULL ,
  executor_timeout number(10) DEFAULT '0' NOT NULL ,
  executor_fail_retry_count number(10) DEFAULT '0' NOT NULL ,
  glue_type varchar2(50) NOT NULL ,
  glue_source clob ,
  glue_remark varchar2(128) DEFAULT NULL ,
  glue_updatetime timestamp(0) DEFAULT NULL ,
  child_jobid varchar2(255) DEFAULT NULL ,
  trigger_status number(3) DEFAULT '0' NOT NULL ,
  trigger_last_time number(19) DEFAULT '0' NOT NULL ,
  trigger_next_time number(19) DEFAULT '0' NOT NULL ,
  PRIMARY KEY (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_info_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_info_seq_tr
 BEFORE INSERT ON xxl_job_info FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_info_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_log (
  id number(19) NOT NULL,
  job_group number(10) NOT NULL ,
  job_id number(10) NOT NULL ,
  executor_address varchar2(255) DEFAULT NULL ,
  executor_handler varchar2(255) DEFAULT NULL ,
  executor_param varchar2(512) DEFAULT NULL ,
  executor_sharding_param varchar2(20) DEFAULT NULL ,
  executor_fail_retry_count number(10) DEFAULT '0' NOT NULL ,
  trigger_time timestamp(0) DEFAULT NULL ,
  trigger_code number(10) NOT NULL ,
  trigger_msg clob ,
  handle_time timestamp(0) DEFAULT NULL ,
  handle_code number(10) NOT NULL ,
  handle_msg clob ,
  alarm_status number(3) DEFAULT '0' NOT NULL ,
  PRIMARY KEY (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_log_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_log_seq_tr
 BEFORE INSERT ON xxl_job_log FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_log_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX I_trigger_time ON xxl_job_log (trigger_time);
CREATE INDEX I_handle_code ON xxl_job_log (handle_code);

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_log_report (
  id number(10) NOT NULL,
  trigger_day timestamp(0) DEFAULT NULL ,
  running_count number(10) DEFAULT '0' NOT NULL ,
  suc_count number(10) DEFAULT '0' NOT NULL ,
  fail_count number(10) DEFAULT '0' NOT NULL ,
  update_time timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT i_trigger_day UNIQUE (trigger_day)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_log_report_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_log_report_seq_tr
 BEFORE INSERT ON xxl_job_log_report FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_log_report_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_logglue (
  id number(10) NOT NULL,
  job_id number(10) NOT NULL ,
  glue_type varchar2(50) DEFAULT NULL ,
  glue_source clob ,
  glue_remark varchar2(128) NOT NULL ,
  add_time timestamp(0) DEFAULT NULL,
  update_time timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_logglue_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_logglue_seq_tr
 BEFORE INSERT ON xxl_job_logglue FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_logglue_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_registry (
  id number(10) NOT NULL,
  registry_group varchar2(50) NOT NULL,
  registry_key varchar2(255) NOT NULL,
  registry_value varchar2(255) NOT NULL,
  update_time timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_registry_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_registry_seq_tr
 BEFORE INSERT ON xxl_job_registry FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_registry_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

CREATE INDEX i_g_k_v ON xxl_job_registry (registry_group,registry_key,registry_value);

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_group (
  id number(10) NOT NULL,
  app_name varchar2(128) NOT NULL ,
  title varchar2(128) NOT NULL ,
  address_type number(3) DEFAULT '0' NOT NULL ,
  address_list clob ,
  update_time timestamp(0) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_group_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_group_seq_tr
 BEFORE INSERT ON xxl_job_group FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_group_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_user (
  id number(10) NOT NULL,
  username varchar2(50) NOT NULL ,
  password varchar2(50) NOT NULL ,
  role number(3) NOT NULL ,
  permission varchar2(255) DEFAULT NULL ,
  PRIMARY KEY (id),
  CONSTRAINT i_username UNIQUE (username)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE xxl_job_user_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER xxl_job_user_seq_tr
 BEFORE INSERT ON xxl_job_user FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT xxl_job_user_seq.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE xxl_job_lock (
  lock_name varchar2(50) NOT NULL ,
  PRIMARY KEY (lock_name)
) ;

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time) VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid) VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO xxl_job_user(id, username, password, role, permission) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');

commit;

