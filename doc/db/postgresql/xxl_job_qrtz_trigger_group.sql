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

Date: 2018-08-28 22:28:34
*/

CREATE SEQUENCE xxl_job_qrtz_trigger_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."xxl_job_qrtz_trigger_group";
CREATE TABLE "public"."xxl_job_qrtz_trigger_group" (
"id" int8 DEFAULT nextval('xxl_job_qrtz_trigger_group_id_seq'::regclass) NOT NULL,
"app_name" varchar(255) COLLATE "default",
"title" varchar(255) COLLATE "default",
"order" int2,
"address_type" int2,
"address_list" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of xxl_job_qrtz_trigger_group
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table xxl_job_qrtz_trigger_group
-- ----------------------------
ALTER TABLE "public"."xxl_job_qrtz_trigger_group" ADD PRIMARY KEY ("id");
