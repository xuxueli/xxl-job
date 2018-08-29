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

Date: 2018-08-28 22:32:04
*/

CREATE SEQUENCE xxl_job_qrtz_trigger_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_log";
CREATE TABLE "public"."xxl_job_qrtz_trigger_log" (
"id" int4 DEFAULT nextval('xxl_job_qrtz_trigger_log_id_seq'::regclass) NOT NULL,
"job_group" int4,
"job_id" int4,
"glue_type" varchar(255) COLLATE "default",
"executor_address" varchar(255) COLLATE "default",
"executor_handler" varchar(255) COLLATE "default",
"executor_param" varchar(255) COLLATE "default",
"trigger_time" timestamp(6),
"trigger_code" int4,
"trigger_msg" varchar(255) COLLATE "default",
"handle_time" timestamp(6),
"handle_code" int4,
"handle_msg" varchar(255) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_log
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Indexes structure for table xxl_job_qrtz_trigger_log
-- ----------------------------
CREATE INDEX "I_trigger_time" ON "public"."xxl_job_qrtz_trigger_log" USING btree ("trigger_time");

-- ----------------------------
-- Primary Key structure for table xxl_job_qrtz_trigger_log
-- ----------------------------
ALTER TABLE "public"."xxl_job_qrtz_trigger_log" ADD PRIMARY KEY ("id", "handle_msg");
