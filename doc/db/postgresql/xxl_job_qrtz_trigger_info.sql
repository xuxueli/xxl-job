/*
Navicat PGSQL Data Transfer

Source Server         : pg
Source Server Version : 90514
Source Host           : localhost:5432
Source Database       : xxl-job
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90514
File Encoding         : 65001

Date: 2018-08-28 22:31:53
*/

CREATE SEQUENCE xxl_job_qrtz_trigger_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_info";
CREATE TABLE "public"."xxl_job_qrtz_trigger_info" (
"id" int4 DEFAULT nextval('xxl_job_qrtz_trigger_info_id_seq'::regclass) NOT NULL,
"job_group" int8,
"job_cron" varchar(255) COLLATE "default",
"job_desc" varchar(255) COLLATE "default",
"add_time" timestamp(6),
"update_time" timestamp(6),
"author" varchar(255) COLLATE "default",
"alarm_email" varchar(255) COLLATE "default",
"executor_route_strategy" varchar(255) COLLATE "default",
"executor_handler" varchar(255) COLLATE "default",
"executor_param" varchar(255) COLLATE "default",
"executor_block_strategy" varchar(255) COLLATE "default",
"executor_fail_strategy" varchar(255) COLLATE "default",
"executor_timeout" int8,
"glue_type" varchar(255) COLLATE "default",
"glue_source" text COLLATE "default",
"glue_remark" varchar(255) COLLATE "default",
"glue_updatetime" timestamp(6),
"child_jobid" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."job_group" IS '执行器主键ID';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."alarm_email" IS '报警邮件';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_route_strategy" IS '执行器路由策略';
COMMENT ON COLUMN "public"."xxl_job_qrtz_trigger_info"."executor_handler" IS '执行器任务handler';

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_info
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table xxl_job_qrtz_trigger_info
-- ----------------------------
ALTER TABLE "public"."xxl_job_qrtz_trigger_info" ADD PRIMARY KEY ("id");
