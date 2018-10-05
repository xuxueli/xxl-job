--drop table job_qrtz_calendars;
--drop table job_qrtz_fired_triggers;
--drop table job_qrtz_blob_triggers;
--drop table job_qrtz_cron_triggers;
--drop table job_qrtz_simple_triggers;
--drop table job_qrtz_simprop_triggers;
--drop table job_qrtz_triggers;
--drop table job_qrtz_job_details;
--drop table job_qrtz_paused_trigger_grps;
--drop table job_qrtz_locks;
--drop table job_qrtz_scheduler_state;


CREATE TABLE job_qrtz_job_details
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL,
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR2(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT job_qrtz_job_details_PK PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE job_qrtz_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT job_qrtz_triggers_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
      REFERENCES job_qrtz_job_details(SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE job_qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    CONSTRAINT QRTZ_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
	REFERENCES job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE job_qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    CONSTRAINT QRTZ_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE job_qrtz_simprop_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    STR_PROP_1 VARCHAR2(512) NULL,
    STR_PROP_2 VARCHAR2(512) NULL,
    STR_PROP_3 VARCHAR2(512) NULL,
    INT_PROP_1 NUMBER(10) NULL,
    INT_PROP_2 NUMBER(10) NULL,
    LONG_PROP_1 NUMBER(13) NULL,
    LONG_PROP_2 NUMBER(13) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    CONSTRAINT QRTZ_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE job_qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    CONSTRAINT QRTZ_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE job_qrtz_calendars
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    CALENDAR_NAME  VARCHAR2(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    CONSTRAINT job_qrtz_calendars_PK PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);
CREATE TABLE job_qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL,
    CONSTRAINT QRTZ_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
CREATE TABLE job_qrtz_fired_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    SCHED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_NONCONCURRENT VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    CONSTRAINT QRTZ_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
CREATE TABLE job_qrtz_scheduler_state
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    CONSTRAINT job_qrtz_scheduler_state_PK PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);
CREATE TABLE job_qrtz_locks
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    LOCK_NAME  VARCHAR2(40) NOT NULL,
    CONSTRAINT job_qrtz_locks_PK PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index idx_qrtz_j_req_recovery on job_qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on job_qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qrtz_t_j on job_qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on job_qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on job_qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on job_qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on job_qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on job_qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);


--drop table JOB_QRTZ_TRIGGER_INFO;
--drop table JOB_QRTZ_TRIGGER_LOG;
--drop table JOB_QRTZ_TRIGGER_GROUP;
CREATE SEQUENCE SEQ_JOB_LOG START WITH 1 INCREMENT BY 1 MAXVALUE 99999999999 MINVALUE 1 NOCYCLE CACHE 20;
CREATE SEQUENCE SEQ_JOB_COMMON START WITH 1 INCREMENT BY 1 MAXVALUE 99999999999 MINVALUE 1 NOCYCLE CACHE 20;


CREATE TABLE JOB_QRTZ_TRIGGER_INFO (
  id NUMBER(11,0),
  job_group NUMBER(11,0) NOT NULL, --执行器主键ID
  job_cron VARCHAR2(128) NOT NULL, --任务执行CRON
  job_desc VARCHAR2(255) NOT NULL,
  add_time DATE,
  update_time DATE,
  author VARCHAR2(64),  -- 作者 
  alarm_email VARCHAR2(255),--报警邮件
  executor_route_strategy VARCHAR2(50), --执行器路由策略
  executor_handler VARCHAR2(255), --执行器任务handler
  executor_param VARCHAR2(512), --执行器任务参数
  executor_block_strategy VARCHAR2(50),--阻塞处理策略
  executor_fail_strategy VARCHAR2(50) ,-- 失败处理策略
  glue_type VARCHAR2(50) NOT NULL, --GLUE类型
  glue_source CLOB,--GLUE源代码'
  glue_remark VARCHAR2(128),--GLUE备注
  glue_updatetime DATE,--GLUE更新时间
  child_jobid VARCHAR2(255),--子任务ID，多个逗号分隔
  PRIMARY KEY (id)
);


CREATE TABLE JOB_QRTZ_TRIGGER_LOG (
  id NUMBER(11,0),
  job_group NUMBER(11,0) NOT NULL,--执行器主键ID
  job_id NUMBER(11,0) NOT NULL,--任务，主键ID
  glue_type VARCHAR2(50),--GLUE类型
  executor_address VARCHAR2(255), --执行器地址，本次执行的地址
  executor_handler VARCHAR2(255) ,--执行器任务handler
  executor_param VARCHAR2(512) ,--执行器任务参数
  trigger_time DATE ,--调度-时间
  trigger_code VARCHAR2(255)  DEFAULT '0' NOT NULL,--调度-结果
  trigger_msg VARCHAR2(2048),--调度-日志
  handle_time DATE ,--执行-时间
  handle_code VARCHAR2(255)  DEFAULT '0' NOT NULL,-- 执行-状态
  handle_msg VARCHAR2(2048),--执行-日志
  PRIMARY KEY (id)
);

CREATE TABLE JOB_QRTZ_TRIGGER_GROUP (
  id NUMBER(11,0),
  app_name VARCHAR2(64) NOT NULL ,-- 执行器AppName
  title VARCHAR2(12) NOT NULL ,--执行器名称
  orderby NUMBER(3,0) DEFAULT '0' NOT NULL  ,--排序
  address_type NUMBER(3,0) DEFAULT '0' NOT NULL ,--执行器地址类型：0=自动注册、1=手动录入
  address_list VARCHAR2(512) ，--执行器地址列表，多地址逗号分隔
  PRIMARY KEY (id)
);


CREATE TABLE JOB_QRTZ_TRIGGER_REGISTRY (
  id NUMBER(11,0),
  registry_group VARCHAR2(255) NOT NULL,
  registry_key VARCHAR2(255) NOT NULL,
  registry_value VARCHAR2(255) NOT NULL,
  update_time DATE  DEFAULT sysdate NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE JOB_QRTZ_TRIGGER_LOGGLUE (
  id NUMBER(11,0),
  job_id NUMBER(11,0) NOT NULL, --任务，主键ID
  glue_type VARCHAR2(50) ,--GLUE类型
  glue_source CLOB,--GLUE源代码
  glue_remark VARCHAR2(128) NOT NULL ,--GLUE备注
  add_time DATE,
  update_time DATE,
  PRIMARY KEY (id)
);

INSERT INTO JOB_QRTZ_TRIGGER_GROUP (id,app_name, title, orderby, address_type, address_list) values (SEQ_JOB_COMMON.nextval,'xxl-job-executor-sample', '示例执行器', '1', '0', null);
commit;










