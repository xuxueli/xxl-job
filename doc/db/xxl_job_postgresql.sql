create SCHEMA xxl_job;

-- ----------------------------
-- Sequence structure for xxl_job_group_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_group_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_group_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for xxl_job_info_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_info_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_info_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for xxl_job_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_log_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_log_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for xxl_job_logglue_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_logglue_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_logglue_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for xxl_job_registry_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_registry_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_registry_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for xxl_job_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "xxl_job"."xxl_job_user_id_seq";
CREATE SEQUENCE "xxl_job"."xxl_job_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_group";
CREATE TABLE "xxl_job"."xxl_job_group" (
  "id" int4 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_group_id_seq'::regclass),
  "app_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "title" varchar(12) COLLATE "pg_catalog"."default" NOT NULL,
  "order" int4 DEFAULT 0 NOT NULL,
  "address_type" int2 DEFAULT 0 NOT NULL,
  "address_list" varchar(512) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_group"."app_name" IS '执行器AppName';
COMMENT ON COLUMN "xxl_job"."xxl_job_group"."title" IS '执行器名称';
COMMENT ON COLUMN "xxl_job"."xxl_job_group"."order" IS '排序';
COMMENT ON COLUMN "xxl_job"."xxl_job_group"."address_type" IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN "xxl_job"."xxl_job_group"."address_list" IS '执行器地址列表，多地址逗号分隔';


-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_info";
CREATE TABLE "xxl_job"."xxl_job_info" (
  "id" int4 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_info_id_seq'::regclass),
  "job_group" int4 NOT NULL,
  "job_cron" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "job_desc" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "add_time" timestamp(6),
  "update_time" timestamp(6),
  "author" varchar(64) COLLATE "pg_catalog"."default",
  "alarm_email" varchar(255) COLLATE "pg_catalog"."default",
  "executor_route_strategy" varchar(50) COLLATE "pg_catalog"."default",
  "executor_handler" varchar(255) COLLATE "pg_catalog"."default",
  "executor_param" varchar(512) COLLATE "pg_catalog"."default",
  "executor_block_strategy" varchar(50) COLLATE "pg_catalog"."default",
  "executor_timeout" int4 DEFAULT 0 NOT NULL,
  "executor_fail_retry_count" int4 DEFAULT 0 NOT NULL,
  "glue_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "glue_source" text COLLATE "pg_catalog"."default",
  "glue_remark" varchar(128) COLLATE "pg_catalog"."default",
  "glue_updatetime" timestamp(6),
  "child_jobid" varchar(255) COLLATE "pg_catalog"."default",
  "trigger_status" int2 DEFAULT 0 NOT NULL,
  "trigger_last_time" int8 DEFAULT 0 NOT NULL,
  "trigger_next_time" int8 DEFAULT 0 NOT NULL
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."job_group" IS '执行器主键ID';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."job_cron" IS '任务执行CRON';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."author" IS '作者';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."alarm_email" IS '报警邮件';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_route_strategy" IS '执行器路由策略';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_handler" IS '执行器任务handler';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_param" IS '执行器任务参数';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_block_strategy" IS '阻塞处理策略';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_timeout" IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."executor_fail_retry_count" IS '失败重试次数';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."glue_type" IS 'GLUE类型';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."glue_source" IS 'GLUE源代码';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."glue_remark" IS 'GLUE备注';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."glue_updatetime" IS 'GLUE更新时间';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."child_jobid" IS '子任务ID，多个逗号分隔';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."trigger_status" IS '调度状态：0-停止，1-运行';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."trigger_last_time" IS '上次调度时间';
COMMENT ON COLUMN "xxl_job"."xxl_job_info"."trigger_next_time" IS '下次调度时间';


-- Table structure for xxl_job_lock
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_lock";
CREATE TABLE "xxl_job"."xxl_job_lock" (
  "lock_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_lock"."lock_name" IS '锁名称';


-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_log";
CREATE TABLE "xxl_job"."xxl_job_log" (
  "id" int8 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_log_id_seq'::regclass),
  "job_group" int4 NOT NULL,
  "job_id" int4 NOT NULL,
  "executor_address" varchar(255) COLLATE "pg_catalog"."default",
  "executor_handler" varchar(255) COLLATE "pg_catalog"."default",
  "executor_param" varchar(512) COLLATE "pg_catalog"."default",
  "executor_sharding_param" varchar(20) COLLATE "pg_catalog"."default",
  "executor_fail_retry_count" int4 DEFAULT 0 NOT NULL,
  "trigger_time" timestamp(6),
  "trigger_code" int4 NOT NULL,
  "trigger_msg" text COLLATE "pg_catalog"."default",
  "handle_time" timestamp(6),
  "handle_code" int4 NOT NULL,
  "handle_msg" text COLLATE "pg_catalog"."default",
  "alarm_status" int2 DEFAULT 0 NOT NULL
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."job_group" IS '执行器主键ID';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."job_id" IS '任务，主键ID';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."executor_address" IS '执行器地址，本次执行的地址';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."executor_handler" IS '执行器任务handler';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."executor_param" IS '执行器任务参数';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."executor_sharding_param" IS '执行器任务分片参数，格式如 1/2';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."executor_fail_retry_count" IS '失败重试次数';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."trigger_time" IS '调度-时间';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."trigger_code" IS '调度-结果';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."trigger_msg" IS '调度-日志';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."handle_time" IS '执行-时间';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."handle_code" IS '执行-状态';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."handle_msg" IS '执行-日志';
COMMENT ON COLUMN "xxl_job"."xxl_job_log"."alarm_status" IS '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';

-- ----------------------------
-- Table structure for xxl_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_logglue";
CREATE TABLE "xxl_job"."xxl_job_logglue" (
  "id" int4 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_logglue_id_seq'::regclass),
  "job_id" int4 NOT NULL,
  "glue_type" varchar(50) COLLATE "pg_catalog"."default",
  "glue_source" text COLLATE "pg_catalog"."default",
  "glue_remark" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "add_time" timestamp(6),
  "update_time" timestamp(6)
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_logglue"."job_id" IS '任务，主键ID';
COMMENT ON COLUMN "xxl_job"."xxl_job_logglue"."glue_type" IS 'GLUE类型';
COMMENT ON COLUMN "xxl_job"."xxl_job_logglue"."glue_source" IS 'GLUE源代码';
COMMENT ON COLUMN "xxl_job"."xxl_job_logglue"."glue_remark" IS 'GLUE备注';

-- 创建自动更新 update_time 字段的触发器及对应function
CREATE FUNCTION "xxl_job".update_timestamp() RETURNS trigger AS $update_timestamp$
    BEGIN
        NEW.update_time := now();
        RETURN NEW;
    END;
$update_timestamp$ LANGUAGE plpgsql;
CREATE TRIGGER update_timestamp BEFORE UPDATE ON "xxl_job"."xxl_job_logglue"
    FOR EACH ROW EXECUTE PROCEDURE "xxl_job".update_timestamp();

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_registry";
CREATE TABLE "xxl_job"."xxl_job_registry" (
  "id" int4 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_registry_id_seq'::regclass),
  "registry_group" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "registry_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "registry_value" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "update_time" timestamp(6) DEFAULT NOW() NOT NULL
)
;

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS "xxl_job"."xxl_job_user";
CREATE TABLE "xxl_job"."xxl_job_user" (
  "id" int4 NOT NULL DEFAULT nextval('"xxl_job".xxl_job_user_id_seq'::regclass),
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "role" int2 NOT NULL,
  "permission" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "xxl_job"."xxl_job_user"."username" IS '账号';
COMMENT ON COLUMN "xxl_job"."xxl_job_user"."password" IS '密码';
COMMENT ON COLUMN "xxl_job"."xxl_job_user"."role" IS '角色：0-普通用户、1-管理员';
COMMENT ON COLUMN "xxl_job"."xxl_job_user"."permission" IS '权限：执行器ID列表，多个逗号分割';


-- ----------------------------
-- Primary Key structure for table xxl_job_group
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_group" ADD CONSTRAINT "xxl_job_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table xxl_job_info
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_info" ADD CONSTRAINT "xxl_job_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table xxl_job_lock
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_lock" ADD CONSTRAINT "xxl_job_lock_pkey" PRIMARY KEY ("lock_name");

-- ----------------------------
-- Indexes structure for table xxl_job_log
-- ----------------------------
CREATE INDEX "I_handle_code" ON "xxl_job"."xxl_job_log" USING btree (
  "handle_code" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "I_trigger_time" ON "xxl_job"."xxl_job_log" USING btree (
  "trigger_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table xxl_job_log
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_log" ADD CONSTRAINT "xxl_job_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table xxl_job_logglue
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_logglue" ADD CONSTRAINT "xxl_job_logglue_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table xxl_job_registry
-- ----------------------------
CREATE INDEX "i_g_k_v" ON "xxl_job"."xxl_job_registry" USING btree (
  "registry_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "registry_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "registry_value" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table xxl_job_registry
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_registry" ADD CONSTRAINT "xxl_job_registry_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table xxl_job_user
-- ----------------------------
CREATE INDEX "i_username" ON "xxl_job"."xxl_job_user" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table xxl_job_user
-- ----------------------------
ALTER TABLE "xxl_job"."xxl_job_user" ADD CONSTRAINT "xxl_job_user_pkey" PRIMARY KEY ("id");



-- ----------------------------
-- Records of xxl_job_user
-- ----------------------------
INSERT INTO "xxl_job"."xxl_job_user" ("username", "password", "role") VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1);

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------
INSERT INTO "xxl_job"."xxl_job_group" ("app_name", "title", "order", "address_type") VALUES ('xxl_job-executor-sample', '示例执行器', 1, 0);

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------
INSERT INTO "xxl_job"."xxl_job_info" ("job_group","job_cron","job_desc","add_time","update_time","author","alarm_email","executor_route_strategy","executor_handler","executor_param","executor_block_strategy","executor_timeout","executor_fail_retry_count","glue_type","glue_source","glue_remark","glue_updatetime","child_jobid") VALUES (1, '0 0 0 * * ? *', '测试任务1', now(), now(), 'XXL', '', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', now(), '');

-- ----------------------------
-- Records of xxl_job_lock
-- ----------------------------
INSERT INTO "xxl_job"."xxl_job_lock" VALUES ('schedule_lock');
-- ----------------------------
