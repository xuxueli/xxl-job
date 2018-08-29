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

Date: 2018-08-28 22:32:21
*/

CREATE SEQUENCE xxl_job_qrtz_trigger_registry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_registry
-- ----------------------------
DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_registry";
CREATE TABLE "public"."xxl_job_qrtz_trigger_registry" (
"id" int4 DEFAULT nextval('xxl_job_qrtz_trigger_registry_id_seq'::regclass) NOT NULL,
"registry_group" varchar(255) COLLATE "default",
"registry_key" varchar(255) COLLATE "default",
"registry_value" varchar(255) COLLATE "default",
"update_time" timestamp(6) default current_timestamp
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_registry
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table xxl_job_qrtz_trigger_registry
-- ----------------------------
ALTER TABLE "public"."xxl_job_qrtz_trigger_registry" ADD PRIMARY KEY ("id");
