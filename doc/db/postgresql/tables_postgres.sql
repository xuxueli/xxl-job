-- Thanks to Patrick Lightbody for submitting this...
--
-- In your Quartz properties file, you'll need to set 
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostgreSQLDelegate

DROP TABLE IF EXISTS  xxl_job_qrtz_fired_triggers;
DROP TABLE IF EXISTS  xxl_job_QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS  xxl_job_QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS  xxl_job_QRTZ_LOCKS;
DROP TABLE IF EXISTS  xxl_job_qrtz_simple_triggers;
DROP TABLE IF EXISTS  xxl_job_qrtz_cron_triggers;
DROP TABLE IF EXISTS  xxl_job_qrtz_simprop_triggers;
DROP TABLE IF EXISTS  xxl_job_QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS  xxl_job_qrtz_triggers;
DROP TABLE IF EXISTS  xxl_job_qrtz_job_details;
DROP TABLE IF EXISTS  xxl_job_qrtz_calendars;

CREATE TABLE xxl_job_qrtz_job_details
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL, 
    IS_DURABLE BOOL NOT NULL,
    IS_NONCONCURRENT BOOL NOT NULL,
    IS_UPDATE_DATA BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE xxl_job_qrtz_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL, 
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) 
	REFERENCES xxl_job_QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP) 
);

CREATE TABLE xxl_job_qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES xxl_job_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE xxl_job_qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES xxl_job_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE xxl_job_qrtz_simprop_triggers
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 BOOL NULL,
    BOOL_PROP_2 BOOL NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES xxl_job_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE xxl_job_qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES xxl_job_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE xxl_job_qrtz_calendars
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL, 
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);


CREATE TABLE xxl_job_qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE xxl_job_qrtz_fired_triggers 
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    SCHED_TIME BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE xxl_job_qrtz_scheduler_state 
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE xxl_job_qrtz_locks
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_group
-- ----------------------------
CREATE SEQUENCE xxl_job_qrtz_trigger_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_group";
CREATE TABLE "public"."xxl_job_qrtz_trigger_group" (
"id" int8 DEFAULT nextval('xxl_job_qrtz_trigger_group_id_seq'::regclass) NOT NULL,
"app_name" varchar(255) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"title" varchar(255) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"order" int4 DEFAULT 0 NOT NULL,
"address_type" int4 DEFAULT 0 NOT NULL,
"address_list" varchar(512) COLLATE "default" DEFAULT ''::character varying,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_group"."app_name" IS '执行器AppName';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_group"."title" IS '执行器名称';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_group"."order" IS '排序';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_group"."address_type" IS '执行器地址类型：0=自动注册、1=手动录入';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_group"."address_list" IS '执行器地址列表，多地址逗号分隔';

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_group
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_info
-- ----------------------------
CREATE SEQUENCE xxl_job_qrtz_trigger_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_info";
CREATE TABLE "public"."xxl_job_qrtz_trigger_info" (
"id" int8 DEFAULT nextval('xxl_job_qrtz_trigger_info_id_seq'::regclass) NOT NULL,
"job_group" int8 DEFAULT 0 NOT NULL,
"job_cron" varchar(128) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"job_desc" varchar(255) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"add_time" timestamp(6) DEFAULT NULL::timestamp without time zone,
"update_time" timestamp(6) DEFAULT NULL::timestamp without time zone,
"author" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"alarm_email" varchar(255) COLLATE "default" DEFAULT NULL::character varying,
"executor_route_strategy" varchar(255) COLLATE "default" DEFAULT NULL::character varying,
"executor_handler" varchar(255) COLLATE "default" DEFAULT NULL::character varying,
"executor_param" varchar(512) COLLATE "default" DEFAULT NULL::character varying,
"executor_block_strategy" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"executor_fail_strategy" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"executor_timeout" int8 DEFAULT 0 NOT NULL,
"glue_type" varchar(255) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"glue_source" text COLLATE "default",
"glue_remark" varchar(255) COLLATE "default" DEFAULT NULL::character varying,
"glue_updatetime" timestamp(6) DEFAULT NULL::timestamp without time zone,
"child_jobid" varchar(255) COLLATE "default" DEFAULT ''::character varying,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."job_group" IS '执行器主键ID';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."job_cron" IS '任务执行CRON';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."job_desc" IS '任务描述';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."author" IS '作者';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."alarm_email" IS '报警邮件';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_route_strategy" IS '执行器路由策略';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_handler" IS '执行器任务handler';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_param" IS '执行器任务参数';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_block_strategy" IS '阻塞处理策略';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_fail_strategy" IS '失败处理策略';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_timeout" IS '任务执行超时时间，单位秒';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."glue_type" IS 'GLUE类型';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."glue_source" IS 'GLUE源代码';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."glue_remark" IS 'GLUE备注';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."glue_updatetime" IS 'GLUE更新时间';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."child_jobid" IS '子任务ID，多个逗号分隔';
-- ----------------------------
-- Records of xxl_job_qrtz_trigger_info
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_log
-- ----------------------------

CREATE SEQUENCE xxl_job_qrtz_trigger_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_log";
CREATE TABLE "public"."xxl_job_qrtz_trigger_log" (
"id" int8 DEFAULT nextval('xxl_job_qrtz_trigger_log_id_seq'::regclass) NOT NULL,
"job_group" int8 DEFAULT 0 NOT NULL,
"job_id" int8 DEFAULT 0 NOT NULL,
"glue_type" varchar(50) COLLATE "default",
"executor_address" varchar(255) COLLATE "default",
"executor_handler" varchar(255) COLLATE "default",
"executor_param" varchar(512) COLLATE "default",
"trigger_time" timestamp(6),
"trigger_code" int8 DEFAULT 0,
"trigger_msg" varchar COLLATE "default" DEFAULT ''::character varying NOT NULL,
"handle_time" timestamp(6),
"handle_code" int8 DEFAULT 0,
"handle_msg" varchar COLLATE "default",
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)
;

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."job_group" IS '执行器主键ID';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."job_id" IS '任务，主键ID';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."glue_type" IS 'GLUE类型';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."executor_address" IS '执行器地址，本次执行的地址';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."executor_handler" IS '执行器任务handler';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."executor_param" IS '执行器任务参数';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."trigger_time" IS '调度-时间';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."trigger_code" IS '调度-结果';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."trigger_msg" IS '调度-日志';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."handle_time" IS '执行-时间';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."handle_code" IS '执行-状态';

COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_log"."handle_msg" IS '执行-日志';

CREATE INDEX "I_trigger_time" ON "public"."xxl_job_qrtz_trigger_log" USING btree ("trigger_time" "pg_catalog"."timestamp_ops");

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_log
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_logglue
-- ----------------------------

CREATE SEQUENCE xxl_job_qrtz_trigger_logglue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_logglue";
CREATE TABLE "public"."xxl_job_qrtz_trigger_logglue" (
"id" int8 DEFAULT nextval('xxl_job_qrtz_trigger_logglue_id_seq'::regclass) NOT NULL,
"job_id" int8 DEFAULT 0 NOT NULL,
"glue_type" varchar(50) COLLATE "default" DEFAULT ''::character varying,
"glue_source" text COLLATE "default",
"glue_remark" varchar(128) COLLATE "default" DEFAULT ''::character varying NOT NULL,
"add_time" timestamp(6) DEFAULT now(),
"update_time" timestamp(6) DEFAULT now(),
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_logglue"."job_id" IS '任务，主键ID';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_logglue"."glue_type" IS 'GLUE类型';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_logglue"."glue_source" IS 'GLUE源代码';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_logglue"."glue_remark" IS 'GLUE备注';
-- ----------------------------
-- Records of xxl_job_qrtz_trigger_logglue
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_registry
-- ----------------------------

CREATE SEQUENCE xxl_job_qrtz_trigger_registry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_registry";
CREATE TABLE "public"."xxl_job_qrtz_trigger_registry" (
"id" int4 DEFAULT nextval('xxl_job_qrtz_trigger_registry_id_seq'::regclass) NOT NULL,
"registry_group" varchar(255) COLLATE "default",
"registry_key" varchar(255) COLLATE "default",
"registry_value" varchar(255) COLLATE "default",
"update_time" timestamp(6) default current_timestamp,
PRIMARY KEY ("id")
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_registry
-- ----------------------------


create index idx_qrtz_j_req_recovery on xxl_job_qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on xxl_job_qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qrtz_t_j on xxl_job_qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on xxl_job_qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on xxl_job_qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on xxl_job_qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on xxl_job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on xxl_job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on xxl_job_qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on xxl_job_qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on xxl_job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on xxl_job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);

INSERT INTO XXL_JOB_QRTZ_TRIGGER_GROUP ( app_name, title, "order", address_type, address_list) values ( 'xxl-job-executor-sample', '示例执行器', '1', '0', null);

commit;
