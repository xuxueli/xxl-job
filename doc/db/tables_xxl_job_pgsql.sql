-- XXL-JOB PostgreSQL Script
-- Copyright (c) 2015-present, xuxueli.

-- 创建数据库（需要在外部执行，或者使用 psql 命令行）
-- CREATE DATABASE xxl_job 
--   ENCODING 'UTF8' 
--   LC_COLLATE 'en_US.utf8' 
--   LC_CTYPE 'en_US.utf8';

-- \c xxl_job;

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 如果有数据库，先转移所有权或删除数据库
--REASSIGN OWNED BY xxl_job TO postgres;  -- 将所有权转移给postgres
--DROP DATABASE IF EXISTS xxl_job; -- 删除用户拥有的所有对象
-- 最后删除用户
--DROP USER IF EXISTS xxl_job;

-- 授予所有必要的权限
GRANT ALL PRIVILEGES ON DATABASE xxl_job TO xxl_job;
GRANT ALL PRIVILEGES ON SCHEMA public TO xxl_job;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO xxl_job;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO xxl_job;

-- 为将来创建的对象设置默认权限
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL PRIVILEGES ON TABLES TO xxl_job;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL PRIVILEGES ON SEQUENCES TO xxl_job;

-- 如果用户需要创建表
GRANT CREATE ON SCHEMA public TO xxl_job;

-- —————————————————————— job group and registry ——————————————————

CREATE TABLE xxl_job_group
(
    id           SERIAL PRIMARY KEY,
    app_name     VARCHAR(64)  NOT NULL,
    title        VARCHAR(64)  NOT NULL,
    address_type SMALLINT     NOT NULL DEFAULT 0,
    address_list TEXT,
    update_time  TIMESTAMP
);

COMMENT ON TABLE xxl_job_group IS '执行器信息表';
COMMENT ON COLUMN xxl_job_group.id IS '主键ID';
COMMENT ON COLUMN xxl_job_group.app_name IS '执行器AppName';
COMMENT ON COLUMN xxl_job_group.title IS '执行器名称';
COMMENT ON COLUMN xxl_job_group.address_type IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN xxl_job_group.address_list IS '执行器地址列表，多地址逗号分隔';
COMMENT ON COLUMN xxl_job_group.update_time IS '更新时间';

CREATE TABLE xxl_job_registry
(
    id             SERIAL PRIMARY KEY,
    registry_group VARCHAR(50)  NOT NULL,
    registry_key   VARCHAR(255) NOT NULL,
    registry_value VARCHAR(255) NOT NULL,
    update_time    TIMESTAMP
);

CREATE UNIQUE INDEX idx_registry_group_key_value ON xxl_job_registry (registry_group, registry_key, registry_value);

COMMENT ON TABLE xxl_job_registry IS '执行器注册表';
COMMENT ON COLUMN xxl_job_registry.id IS '主键ID';
COMMENT ON COLUMN xxl_job_registry.registry_group IS '注册组';
COMMENT ON COLUMN xxl_job_registry.registry_key IS '注册键';
COMMENT ON COLUMN xxl_job_registry.registry_value IS '注册值';
COMMENT ON COLUMN xxl_job_registry.update_time IS '更新时间';

-- —————————————————————— job info ——————————————————

CREATE TABLE xxl_job_info
(
    id                        SERIAL PRIMARY KEY,
    job_group                 INTEGER       NOT NULL,
    job_desc                  VARCHAR(255)  NOT NULL,
    add_time                  TIMESTAMP,
    update_time               TIMESTAMP,
    author                    VARCHAR(64),
    alarm_email               VARCHAR(255),
    schedule_type             VARCHAR(50)   NOT NULL DEFAULT 'NONE',
    schedule_conf             VARCHAR(128),
    misfire_strategy          VARCHAR(50)   NOT NULL DEFAULT 'DO_NOTHING',
    executor_route_strategy   VARCHAR(50),
    executor_handler          VARCHAR(255),
    executor_param            VARCHAR(512),
    executor_block_strategy   VARCHAR(50),
    executor_timeout          INTEGER       NOT NULL DEFAULT 0,
    executor_fail_retry_count INTEGER       NOT NULL DEFAULT 0,
    glue_type                 VARCHAR(50)   NOT NULL,
    glue_source               TEXT,
    glue_remark               VARCHAR(128),
    glue_updatetime           TIMESTAMP,
    child_jobid               VARCHAR(255),
    trigger_status            SMALLINT      NOT NULL DEFAULT 0,
    trigger_last_time         BIGINT        NOT NULL DEFAULT 0,
    trigger_next_time         BIGINT        NOT NULL DEFAULT 0
);

COMMENT ON TABLE xxl_job_info IS '任务信息表';
COMMENT ON COLUMN xxl_job_info.id IS '主键ID';
COMMENT ON COLUMN xxl_job_info.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_info.job_desc IS '任务描述';
COMMENT ON COLUMN xxl_job_info.add_time IS '添加时间';
COMMENT ON COLUMN xxl_job_info.update_time IS '更新时间';
COMMENT ON COLUMN xxl_job_info.author IS '作者';
COMMENT ON COLUMN xxl_job_info.alarm_email IS '报警邮件';
COMMENT ON COLUMN xxl_job_info.schedule_type IS '调度类型';
COMMENT ON COLUMN xxl_job_info.schedule_conf IS '调度配置，值含义取决于调度类型';
COMMENT ON COLUMN xxl_job_info.misfire_strategy IS '调度过期策略';
COMMENT ON COLUMN xxl_job_info.executor_route_strategy IS '执行器路由策略';
COMMENT ON COLUMN xxl_job_info.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_info.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_info.executor_block_strategy IS '阻塞处理策略';
COMMENT ON COLUMN xxl_job_info.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN xxl_job_info.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_info.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_info.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_info.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_info.glue_updatetime IS 'GLUE更新时间';
COMMENT ON COLUMN xxl_job_info.child_jobid IS '子任务ID，多个逗号分隔';
COMMENT ON COLUMN xxl_job_info.trigger_status IS '调度状态：0-停止，1-运行';
COMMENT ON COLUMN xxl_job_info.trigger_last_time IS '上次调度时间';
COMMENT ON COLUMN xxl_job_info.trigger_next_time IS '下次调度时间';

CREATE TABLE xxl_job_logglue
(
    id          SERIAL PRIMARY KEY,
    job_id      INTEGER       NOT NULL,
    glue_type   VARCHAR(50),
    glue_source TEXT,
    glue_remark VARCHAR(128)  NOT NULL,
    add_time    TIMESTAMP,
    update_time TIMESTAMP
);

COMMENT ON TABLE xxl_job_logglue IS '任务GLUE日志表';
COMMENT ON COLUMN xxl_job_logglue.id IS '主键ID';
COMMENT ON COLUMN xxl_job_logglue.job_id IS '任务，主键ID';
COMMENT ON COLUMN xxl_job_logglue.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_logglue.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_logglue.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_logglue.add_time IS '添加时间';
COMMENT ON COLUMN xxl_job_logglue.update_time IS '更新时间';

-- —————————————————————— job log and report ——————————————————

CREATE TABLE xxl_job_log
(
    id                        BIGSERIAL PRIMARY KEY,
    job_group                 INTEGER      NOT NULL,
    job_id                    INTEGER      NOT NULL,
    executor_address          VARCHAR(255),
    executor_handler          VARCHAR(255),
    executor_param            VARCHAR(512),
    executor_sharding_param   VARCHAR(20),
    executor_fail_retry_count INTEGER      NOT NULL DEFAULT 0,
    trigger_time              TIMESTAMP,
    trigger_code              INTEGER      NOT NULL,
    trigger_msg               TEXT,
    handle_time               TIMESTAMP,
    handle_code               INTEGER      NOT NULL,
    handle_msg                TEXT,
    alarm_status              SMALLINT     NOT NULL DEFAULT 0
);

CREATE INDEX idx_job_log_trigger_time ON xxl_job_log (trigger_time);
CREATE INDEX idx_job_log_handle_code ON xxl_job_log (handle_code);
CREATE INDEX idx_job_log_job_id_group ON xxl_job_log (job_id, job_group);
CREATE INDEX idx_job_log_job_id ON xxl_job_log (job_id);

COMMENT ON TABLE xxl_job_log IS '任务日志表';
COMMENT ON COLUMN xxl_job_log.id IS '主键ID';
COMMENT ON COLUMN xxl_job_log.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_log.job_id IS '任务，主键ID';
COMMENT ON COLUMN xxl_job_log.executor_address IS '执行器地址，本次执行的地址';
COMMENT ON COLUMN xxl_job_log.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN xxl_job_log.executor_param IS '执行器任务参数';
COMMENT ON COLUMN xxl_job_log.executor_sharding_param IS '执行器任务分片参数，格式如 1/2';
COMMENT ON COLUMN xxl_job_log.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_log.trigger_time IS '调度-时间';
COMMENT ON COLUMN xxl_job_log.trigger_code IS '调度-结果';
COMMENT ON COLUMN xxl_job_log.trigger_msg IS '调度-日志';
COMMENT ON COLUMN xxl_job_log.handle_time IS '执行-时间';
COMMENT ON COLUMN xxl_job_log.handle_code IS '执行-状态';
COMMENT ON COLUMN xxl_job_log.handle_msg IS '执行-日志';
COMMENT ON COLUMN xxl_job_log.alarm_status IS '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';

CREATE TABLE xxl_job_log_report
(
    id            SERIAL PRIMARY KEY,
    trigger_day   TIMESTAMP,
    running_count INTEGER      NOT NULL DEFAULT 0,
    suc_count     INTEGER      NOT NULL DEFAULT 0,
    fail_count    INTEGER      NOT NULL DEFAULT 0,
    update_time   TIMESTAMP
);

CREATE UNIQUE INDEX idx_log_report_trigger_day ON xxl_job_log_report (trigger_day);

COMMENT ON TABLE xxl_job_log_report IS '日志报告表';
COMMENT ON COLUMN xxl_job_log_report.id IS '主键ID';
COMMENT ON COLUMN xxl_job_log_report.trigger_day IS '调度-时间';
COMMENT ON COLUMN xxl_job_log_report.running_count IS '运行中-日志数量';
COMMENT ON COLUMN xxl_job_log_report.suc_count IS '执行成功-日志数量';
COMMENT ON COLUMN xxl_job_log_report.fail_count IS '执行失败-日志数量';
COMMENT ON COLUMN xxl_job_log_report.update_time IS '更新时间';

-- —————————————————————— lock ——————————————————

CREATE TABLE xxl_job_lock
(
    lock_name VARCHAR(50) PRIMARY KEY
);

COMMENT ON TABLE xxl_job_lock IS '分布式锁表';
COMMENT ON COLUMN xxl_job_lock.lock_name IS '锁名称';

-- —————————————————————— user ——————————————————

CREATE TABLE xxl_job_user
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    token      VARCHAR(100),
    role       SMALLINT     NOT NULL,
    permission VARCHAR(255)
);

CREATE UNIQUE INDEX idx_user_username ON xxl_job_user (username);

COMMENT ON TABLE xxl_job_user IS '用户表';
COMMENT ON COLUMN xxl_job_user.id IS '主键ID';
COMMENT ON COLUMN xxl_job_user.username IS '账号';
COMMENT ON COLUMN xxl_job_user.password IS '密码加密信息';
COMMENT ON COLUMN xxl_job_user.token IS '登录token';
COMMENT ON COLUMN xxl_job_user.role IS '角色：0-普通用户、1-管理员';
COMMENT ON COLUMN xxl_job_user.permission IS '权限：执行器ID列表，多个逗号分割';

-- —————————————————————— for default data ——————————————————

INSERT INTO xxl_job_group (id, app_name, title, address_type, address_list, update_time)
VALUES (1, 'xxl-job-executor-sample', '通用执行器Sample', 0, NULL, NOW()),
       (2, 'xxl-job-executor-sample-ai', 'AI执行器Sample', 0, NULL, NOW());

-- 设置序列的起始值
SELECT setval('xxl_job_group_id_seq', (SELECT MAX(id) FROM xxl_job_group));

INSERT INTO xxl_job_info (id, job_group, job_desc, add_time, update_time, author, alarm_email,
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
    "prompt": "你是一个研发工程师，擅长解决技术类问题。",
    "model": "qwen3:0.6b"
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
        NOW(), '');

-- 设置序列的起始值
SELECT setval('xxl_job_info_id_seq', (SELECT MAX(id) FROM xxl_job_info));

INSERT INTO xxl_job_user (id, username, password, role, permission)
VALUES (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, NULL);

-- 设置序列的起始值
SELECT setval('xxl_job_user_id_seq', (SELECT MAX(id) FROM xxl_job_user));

INSERT INTO xxl_job_lock (lock_name)
VALUES ('schedule_lock');


