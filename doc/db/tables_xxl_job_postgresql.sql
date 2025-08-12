--
-- XXL-JOB
-- Copyright (c) 2015-present, xuxueli.
-- PostgreSQL version

CREATE DATABASE xxl_job;

-- Connect to xxl_job database
\c xxl_job;

CREATE TABLE xxl_job_info
(
    id                        SERIAL PRIMARY KEY,
    job_group                 INT          NOT NULL,
    job_desc                  VARCHAR(255) NOT NULL,
    add_time                  TIMESTAMP    DEFAULT NULL,
    update_time               TIMESTAMP    DEFAULT NULL,
    author                    VARCHAR(64)  DEFAULT NULL,
    alarm_email               VARCHAR(255) DEFAULT NULL,
    schedule_type             VARCHAR(50)  NOT NULL DEFAULT 'NONE',
    schedule_conf             VARCHAR(128) DEFAULT NULL,
    misfire_strategy          VARCHAR(50)  NOT NULL DEFAULT 'DO_NOTHING',
    executor_route_strategy   VARCHAR(50)  DEFAULT NULL,
    executor_handler          VARCHAR(255) DEFAULT NULL,
    executor_param            VARCHAR(512) DEFAULT NULL,
    executor_block_strategy   VARCHAR(50)  DEFAULT NULL,
    executor_timeout          INT          NOT NULL DEFAULT 0,
    executor_fail_retry_count INT          NOT NULL DEFAULT 0,
    glue_type                 VARCHAR(50)  NOT NULL,
    glue_source               TEXT,
    glue_remark               VARCHAR(128) DEFAULT NULL,
    glue_updatetime           TIMESTAMP    DEFAULT NULL,
    child_jobid               VARCHAR(255) DEFAULT NULL,
    trigger_status            SMALLINT     NOT NULL DEFAULT 0,
    trigger_last_time         BIGINT       NOT NULL DEFAULT 0,
    trigger_next_time         BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE xxl_job_log
(
    id                        BIGSERIAL PRIMARY KEY,
    job_group                 INT          NOT NULL,
    job_id                    INT          NOT NULL,
    executor_address          VARCHAR(255) DEFAULT NULL,
    executor_handler          VARCHAR(255) DEFAULT NULL,
    executor_param            VARCHAR(512) DEFAULT NULL,
    executor_sharding_param   VARCHAR(20)  DEFAULT NULL,
    executor_fail_retry_count INT          NOT NULL DEFAULT 0,
    trigger_time              TIMESTAMP    DEFAULT NULL,
    trigger_code              INT          NOT NULL,
    trigger_msg               TEXT,
    handle_time               TIMESTAMP    DEFAULT NULL,
    handle_code               INT          NOT NULL,
    handle_msg                TEXT,
    alarm_status              SMALLINT     NOT NULL DEFAULT 0
);

CREATE INDEX I_trigger_time ON xxl_job_log (trigger_time);
CREATE INDEX I_handle_code ON xxl_job_log (handle_code);
CREATE INDEX I_jobid_jobgroup ON xxl_job_log (job_id, job_group);
CREATE INDEX I_job_id ON xxl_job_log (job_id);

CREATE TABLE xxl_job_log_report
(
    id            SERIAL PRIMARY KEY,
    trigger_day   TIMESTAMP DEFAULT NULL,
    running_count INT NOT NULL DEFAULT 0,
    suc_count     INT NOT NULL DEFAULT 0,
    fail_count    INT NOT NULL DEFAULT 0,
    update_time   TIMESTAMP DEFAULT NULL
);

CREATE UNIQUE INDEX i_trigger_day ON xxl_job_log_report (trigger_day);

CREATE TABLE xxl_job_logglue
(
    id          SERIAL PRIMARY KEY,
    job_id      INT NOT NULL,
    glue_type   VARCHAR(50) DEFAULT NULL,
    glue_source TEXT,
    glue_remark VARCHAR(128) NOT NULL,
    add_time    TIMESTAMP DEFAULT NULL,
    update_time TIMESTAMP DEFAULT NULL
);

CREATE TABLE xxl_job_registry
(
    id             SERIAL PRIMARY KEY,
    registry_group VARCHAR(50) NOT NULL,
    registry_key   VARCHAR(255) NOT NULL,
    registry_value VARCHAR(255) NOT NULL,
    update_time    TIMESTAMP DEFAULT NULL
);

CREATE UNIQUE INDEX i_g_k_v ON xxl_job_registry (registry_group, registry_key, registry_value);

CREATE TABLE xxl_job_group
(
    id           SERIAL PRIMARY KEY,
    app_name     VARCHAR(64) NOT NULL,
    title        VARCHAR(12) NOT NULL,
    address_type SMALLINT NOT NULL DEFAULT 0,
    address_list TEXT,
    update_time  TIMESTAMP DEFAULT NULL
);

CREATE TABLE xxl_job_user
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50) NOT NULL,
    password   VARCHAR(50) NOT NULL,
    role       SMALLINT NOT NULL,
    permission VARCHAR(255) DEFAULT NULL
);

CREATE UNIQUE INDEX i_username ON xxl_job_user (username);

CREATE TABLE xxl_job_lock
(
    lock_name VARCHAR(50) NOT NULL PRIMARY KEY
);


-- ——————————————————————————————————— init data ———————————————————————————————————

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time)
    VALUES (1, 'xxl-job-executor-sample', '通用执行器Sample', 0, NULL, NOW()),
           (2, 'xxl-job-executor-sample-ai', 'AI执行器Sample', 0, NULL, NOW());

-- Reset sequence to continue from inserted values
SELECT setval('xxl_job_group_id_seq', 2, true);

INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email,
                        schedule_type, schedule_conf, misfire_strategy, executor_route_strategy,
                        executor_handler, executor_param, executor_block_strategy, executor_timeout,
                        executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime,
                        child_jobid)
VALUES (1, 1, '示例任务01', NOW(), NOW(), 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        NOW(), ''),
       (2, 2, 'Ollama示例任务01', NOW(), NOW(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'ollamaJobHandler', '{
    "input": "慢SQL问题分析思路",
    "prompt": "你是一个研发工程师，擅长解决技术类问题。"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        NOW(), ''),
       (3, 2, 'Dify示例任务', NOW(), NOW(), 'XXL', '', 'NONE', '',
        'DO_NOTHING', 'FIRST', 'difyWorkflowJobHandler', '{
    "inputs":{
        "input":"查询班级各学科前三名"
    },
    "user": "xxl-job",
    "baseUrl": "http://localhost/v1",
    "apiKey": "app-OUVgNUOQRIMokfmuJvBJoUTN"
}', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        NOW(), '')
    ;

-- Reset sequence to continue from inserted values
SELECT setval('xxl_job_info_id_seq', 3, true);

INSERT INTO xxl_job_user(id, username, password, role, permission)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);

-- Reset sequence to continue from inserted values
SELECT setval('xxl_job_user_id_seq', 1, true);

INSERT INTO xxl_job_lock (lock_name)
VALUES ('schedule_lock');

COMMIT;