/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : xxl_job

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 21/09/2023 23:17:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xxl_job_glue_log
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_glue_log`;
CREATE TABLE IF NOT EXISTS `xxl_job_glue_log`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `JOB_ID` bigint NOT NULL COMMENT '任务，主键ID',
  `GLUE_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'GLUE类型',
  `GLUE_SOURCE` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'GLUE源代码',
  `CREATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `idx_job_id`(`JOB_ID` ASC) USING BTREE COMMENT '任务ID索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'GLUE日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_glue_log
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE IF NOT EXISTS `xxl_job_group`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `APP_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器AppName',
  `TITLE` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器名称',
  `ADDRESS_TYPE` int NOT NULL DEFAULT 0 COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `ADDRESS_LIST` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行器地址列表，多地址逗号分隔',
  `CREATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `idx_name`(`APP_NAME` ASC) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '执行器组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE IF NOT EXISTS `xxl_job_info`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `GROUP_ID` bigint NOT NULL COMMENT '执行器主键ID',
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名',
  `AUTHOR` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者',
  `ALARM_EMAIL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报警邮件',
  `SCHEDULE_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度类型',
  `SCHEDULE_CONF` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
  `MISFIRE_STRATEGY` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
  `EXECUTOR_ROUTE_STRATEGY` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'FIRST' COMMENT '执行器路由策略',
  `EXECUTOR_HANDLER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
  `EXECUTOR_PARAM` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行器任务参数',
  `EXECUTOR_BLOCK_STRATEGY` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'SERIAL_EXECUTION' COMMENT '阻塞处理策略',
  `EXECUTOR_TIMEOUT` int NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
  `EXECUTOR_FAIL_RETRY_COUNT` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
  `GLUE_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'GLUE类型',
  `GLUE_SOURCE` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'GLUE源码',
  `GLUE_DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'GLUE描述',
  `GLUE_UPDATED_TIME` datetime NULL DEFAULT NULL COMMENT 'GLUE修改时间',
  `CHILD_JOB_ID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `KETTLE_ID` bigint NULL DEFAULT NULL COMMENT 'kettle信息ID',
  `TRIGGER_STATUS` int NOT NULL COMMENT '调度状态：0-停止，1-运行',
  `TRIGGER_LAST_TIME` datetime NULL DEFAULT NULL COMMENT '上次调度时间',
  `TRIGGER_NEXT_TIME` datetime NULL DEFAULT NULL COMMENT '下次调度时间',
  `CREATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `CREATED_TIME` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `UPDATED_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `UPDATED_TIME` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `idx_name`(`NAME` ASC) USING BTREE COMMENT '任务名索引',
  INDEX `idx_group`(`GROUP_ID` ASC) USING BTREE COMMENT '执行器索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_kettle_info
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_kettle_info`;
CREATE TABLE IF NOT EXISTS `xxl_job_kettle_info`  (
  `id` bigint UNSIGNED NOT NULL COMMENT ' 主键',
  `series` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系列',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `type` enum('KTR','KJB') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'KTR' COMMENT '模型类型(ktr,kjb)',
  `kettle_file` longblob NOT NULL COMMENT 'kettle文件',
  `guide_kjb` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'kjb引导文件，模型类型为kjb有效',
  `log_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `version` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态, 1: 启用, 0:禁用',
  `created_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE COMMENT '编码索引',
  UNIQUE INDEX `idx_unique`(`name` ASC, `version` ASC) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'kettle信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_kettle_info
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_lock
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE IF NOT EXISTS `xxl_job_lock`  (
  `LOCK_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务锁' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_lock
-- ----------------------------
INSERT IGNORE INTO `xxl_job_lock` VALUES ('schedule_lock');

-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE IF NOT EXISTS `xxl_job_log`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `GROUP_ID` bigint NOT NULL COMMENT '执行器主键ID',
  `JOB_ID` bigint NOT NULL COMMENT '任务，主键ID',
  `EXECUTOR_ADDRESS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `EXECUTOR_HANDLER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
  `EXECUTOR_PARAM` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行器任务参数',
  `EXECUTOR_SHARDING_PARAM` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `EXECUTOR_FAIL_RETRY_COUNT` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
  `TRIGGER_TIME` datetime NULL DEFAULT NULL COMMENT '调度-时间',
  `TRIGGER_CODE` int NULL DEFAULT -1 COMMENT '调度-结果 (-1: 无效, 0:成功, 其他:失败)',
  `TRIGGER_MESSAGE` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '调度-日志',
  `HANDLE_TIME` datetime NULL DEFAULT NULL COMMENT '执行-时间',
  `HANDLE_CODE` int NOT NULL DEFAULT -1 COMMENT '执行-状态(-1: 运行中,0:成功,其他:失败)',
  `HANDLE_MESSAGE` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行-日志',
  `ALARM_STATUS` int NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `idx_job`(`JOB_ID` ASC) USING BTREE COMMENT '任务ID索引',
  INDEX `idx_trigger_time`(`TRIGGER_TIME` ASC) USING BTREE COMMENT '调度时间索引',
  INDEX `idx_handle_code`(`HANDLE_CODE` ASC) USING BTREE COMMENT '执行结果索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务日志信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_log_report
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE IF NOT EXISTS `xxl_job_log_report`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `TRIGGER_DAY` date NOT NULL COMMENT '调度-时间',
  `RUNNING_COUNT` bigint NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
  `SUC_COUNT` bigint NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
  `FAIL_COUNT` bigint NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `idx_trigger_day`(`TRIGGER_DAY` ASC) USING BTREE COMMENT '调度时间索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务日志报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_log_report
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_login_token
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_login_token`;
CREATE TABLE IF NOT EXISTS `xxl_job_login_token`  (
  `ID` bigint UNSIGNED NOT NULL COMMENT '主键',
  `TOKEN` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录令牌',
  `ACCOUNT` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `EFFECTIVE_DURATION` int NOT NULL COMMENT '有效时长(单位：秒)',
  `LOGIN_TIME` datetime NOT NULL COMMENT '登录时间',
  `UPDATED_TIME` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `idx_unique`(`TOKEN` ASC) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_login_token
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE IF NOT EXISTS `xxl_job_registry`  (
  `ID` bigint NOT NULL COMMENT '主键',
  `REGISTRY_GROUP` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册分组',
  `REGISTRY_KEY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册KEY',
  `REGISTRY_VALUE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '注册value',
  `UPDATED_TIME` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `i_g_k_v`(`REGISTRY_GROUP` ASC, `REGISTRY_KEY` ASC, `REGISTRY_VALUE` ASC) USING BTREE COMMENT '联合索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务注册信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_registry
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_user_info
-- ----------------------------
# DROP TABLE IF EXISTS `xxl_job_user_info`;
CREATE TABLE IF NOT EXISTS `xxl_job_user_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `mail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sex` int NULL DEFAULT NULL COMMENT '性别  男：0，女：1',
  `telephone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `status` int NULL DEFAULT 1 COMMENT '账号状态, (0->已过期，1->启用，-1->禁用 )',
  `created_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_index_account`(`account` ASC) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_user_info
-- ----------------------------
INSERT IGNORE INTO `xxl_job_user_info` VALUES (1683039670926716929, 'admin', '$2a$10$VNJuZ/j8j6goIhgesp7unejvNKQGdTj4JSnb8OJ3/kLLwgz3Kyu3e', '贾荣', '', 0, '', 1, NULL, '2023-09-21 21:57:04', NULL, NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
