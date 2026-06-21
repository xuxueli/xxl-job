--
-- XXL-JOB
-- Copyright (c) 2015-present, xuxueli.

CREATE DATABASE IF NOT EXISTS `xxl_job` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `xxl_job`;

-- ================== job group and registry ==================

CREATE TABLE `xxl_job_group`
(
    `id`                    INT                 NOT NULL AUTO_INCREMENT,
    `app_name`              VARCHAR(64)         NOT NULL                COMMENT '执行器AppName',
    `name`                  VARCHAR(64)         NOT NULL                COMMENT '执行器名称',
    `address_type`          TINYINT             NOT NULL DEFAULT '0'    COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list`          TEXT                DEFAULT NULL            COMMENT '执行器地址列表，多地址逗号分隔',
    `access_token`          VARCHAR(255)        DEFAULT NULL            COMMENT '执行器AccessToken',
    `update_time`           DATETIME            DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_app_name` (`app_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `xxl_job_registry`
(
    `id`                    BIGINT              NOT NULL AUTO_INCREMENT,
    `registry_group`        VARCHAR(50)         NOT NULL,
    `registry_key`          VARCHAR(255)        NOT NULL,
    `registry_value`        VARCHAR(255)        NOT NULL,
    `update_time`           DATETIME            DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_g_k_v` (`registry_group`, `registry_key`, `registry_value`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ================== job info ==================

CREATE TABLE `xxl_job_info`
(
    `id`                        INT                 NOT NULL AUTO_INCREMENT,
    `job_group`                 INT                 NOT NULL                        COMMENT '执行器主键ID',
    `name`                      VARCHAR(255)        NOT NULL                        COMMENT '执行器名称',
    `author`                    VARCHAR(64)         DEFAULT NULL                    COMMENT '作者',
    `alarm_email`               VARCHAR(255)        DEFAULT NULL                    COMMENT '报警邮件',
    `schedule_type`             VARCHAR(50)         NOT NULL DEFAULT 'NONE'         COMMENT '调度类型',
    `schedule_conf`             VARCHAR(128)        DEFAULT NULL                    COMMENT '调度配置，值含义取决于调度类型',
    `misfire_strategy`          VARCHAR(50)         NOT NULL DEFAULT 'DO_NOTHING'   COMMENT '调度过期策略',
    `executor_route_strategy`   VARCHAR(50)         DEFAULT NULL                    COMMENT '执行器路由策略',
    `executor_handler`          VARCHAR(255)        DEFAULT NULL                    COMMENT '任务handler',
    `executor_param`            TEXT                DEFAULT NULL                    COMMENT '任务参数',
    `executor_block_strategy`   VARCHAR(50)         DEFAULT NULL                    COMMENT '阻塞处理策略',
    `executor_timeout`          INT                 NOT NULL DEFAULT '0'            COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` INT                 NOT NULL DEFAULT '0'            COMMENT '失败重试次数',
    `glue_type`                 VARCHAR(50)         NOT NULL                        COMMENT 'GLUE类型',
    `glue_source`               MEDIUMTEXT          DEFAULT NULL                    COMMENT 'GLUE源代码',
    `glue_remark`               VARCHAR(128)        DEFAULT NULL                    COMMENT 'GLUE备注',
    `glue_updatetime`           DATETIME            DEFAULT NULL                    COMMENT 'GLUE更新时间',
    `child_jobid`               VARCHAR(255)        DEFAULT NULL                    COMMENT '子任务ID，多个逗号分隔',
    `trigger_status`            TINYINT             NOT NULL DEFAULT '0'            COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time`         BIGINT              NOT NULL DEFAULT '0'            COMMENT '上次调度时间',
    `trigger_next_time`         BIGINT              NOT NULL DEFAULT '0'            COMMENT '下次调度时间',
    `add_time`                  DATETIME            DEFAULT NULL,
    `update_time`               DATETIME            DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `xxl_job_logglue`
(
    `id`                        INT                 NOT NULL AUTO_INCREMENT,
    `job_id`                    INT                 NOT NULL                COMMENT '任务，主键ID',
    `glue_type`                 VARCHAR(50)         DEFAULT NULL            COMMENT 'GLUE类型',
    `glue_source`               MEDIUMTEXT          DEFAULT NULL            COMMENT 'GLUE源代码',
    `glue_remark`               VARCHAR(128)        NOT NULL                COMMENT 'GLUE备注',
    `add_time`                  DATETIME            DEFAULT NULL,
    `update_time`               DATETIME            DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ================== job log and report ==================

CREATE TABLE `xxl_job_log`
(
    `id`                        BIGINT              NOT NULL AUTO_INCREMENT,
    `job_group`                 INT                 NOT NULL                COMMENT '执行器主键ID',
    `job_id`                    INT                 NOT NULL                COMMENT '任务，主键ID',
    `executor_address`          VARCHAR(255)        DEFAULT NULL            COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          VARCHAR(255)        DEFAULT NULL            COMMENT '任务handler',
    `executor_param`            TEXT                DEFAULT NULL            COMMENT '任务参数',
    `executor_sharding_param`   VARCHAR(20)         DEFAULT NULL            COMMENT '任务分片参数，格式如 1/2',
    `executor_fail_retry_count` INT                 NOT NULL DEFAULT '0'    COMMENT '失败重试次数',
    `trigger_time`              DATETIME            DEFAULT NULL            COMMENT '调度-时间',
    `trigger_code`              INT                 NOT NULL                COMMENT '调度-结果',
    `trigger_msg`               TEXT                DEFAULT NULL            COMMENT '调度-日志',
    `handle_time`               DATETIME            DEFAULT NULL            COMMENT '执行-时间',
    `handle_code`               INT                 NOT NULL                COMMENT '执行-状态',
    `handle_msg`                TEXT                DEFAULT NULL            COMMENT '执行-日志',
    `alarm_status`              TINYINT             NOT NULL DEFAULT '0'    COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY `i_trigger_time` (`trigger_time`),
    KEY `i_handle_code` (`handle_code`),
    KEY `i_job_group` (`job_group`),
    KEY `i_job_id` (`job_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `xxl_job_log_report`
(
    `id`                    INT             NOT NULL AUTO_INCREMENT,
    `trigger_day`           DATETIME        DEFAULT NULL            COMMENT '调度-时间',
    `running_count`         INT             NOT NULL DEFAULT '0'    COMMENT '运行中-日志数量',
    `suc_count`             INT             NOT NULL DEFAULT '0'    COMMENT '执行成功-日志数量',
    `fail_count`            INT             NOT NULL DEFAULT '0'    COMMENT '执行失败-日志数量',
    `update_time`           DATETIME        DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ================== lock ==================

CREATE TABLE `xxl_job_lock`
(
    `lock_name` VARCHAR(50) NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ================== user ==================

CREATE TABLE `xxl_job_user`
(
    `id`                INT                 NOT NULL AUTO_INCREMENT,
    `username`          VARCHAR(50)         NOT NULL            COMMENT '账号',
    `password`          VARCHAR(100)        NOT NULL            COMMENT '密码加密信息',
    `token`             VARCHAR(100)        DEFAULT NULL        COMMENT '登录token',
    `role`              TINYINT             NOT NULL            COMMENT '角色：0-普通用户、1-管理员',
    `permission`        VARCHAR(255)        DEFAULT NULL        COMMENT '权限：执行器ID列表，多个逗号分割',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- ================== for default data ==================

START TRANSACTION;

INSERT INTO `xxl_job_group`(`id`, `app_name`, `name`, `address_type`, `address_list`, `access_token`, `update_time`)
    VALUES (1, 'xxl-job-executor-sample', '通用执行器Sample', 0,  NULL, 'default_token', now()),
           (2, 'xxl-job-executor-sample-ai', 'AI执行器Sample', 0, NULL, 'default_token', now());

INSERT INTO `xxl_job_info`(`id`, `job_group`, `name`, `add_time`, `update_time`, `author`, `alarm_email`,
                           `schedule_type`, `schedule_conf`, `misfire_strategy`, `executor_route_strategy`,
                           `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`,
                           `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`,
                           `child_jobid`)
VALUES (1, 1, '示例任务01', now(), now(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (2, 2, 'Ollama示例任务', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "Java实现二叉树层序遍历",
    "prompt": "你是一个研发工程师，擅长解决技术类问题。",
    "model": "qwen3.5:0.8b"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (3, 2, 'Dify示例任务', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"查询班级各学科前三名"
    },
    "user": "xxl-job",
    "baseUrl": "http://localhost/v1",
    "apiKey": "app-OUVgNUOQRIMokfmuJvBJoUTN"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), ''),
       (4, 2, 'OpenClaw示例任务', now(), now(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'openClawJobHandler', '{
    "input": "查看下上海今天得天气，给出出游建议",
    "prompt": "你是一个出游助手，擅长做旅游规划"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        now(), '');

INSERT INTO `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`)
VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, NULL);

INSERT INTO `xxl_job_lock` (`lock_name`)
VALUES ('schedule_lock');

commit;
