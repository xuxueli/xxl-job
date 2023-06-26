create database xxl_job;
\c xxl_job;
\connect xxl_job;
SET search_path = public;
\c xxl_job.public;
CREATE TABLE xxl_job_info
(
    id                        SERIAL PRIMARY KEY,
    job_group                 int      NOT NULL,
    job_desc                  text     NOT NULL,
    add_time                  timestamp         DEFAULT NULL,
    update_time               timestamp         DEFAULT NULL,
    author                    text              DEFAULT NULL,
    alarm_email               text              DEFAULT NULL,
    schedule_type             text     NOT NULL DEFAULT 'NONE',
    schedule_conf             text              DEFAULT NULL,
    misfire_strategy          text     NOT NULL DEFAULT 'DO_NOTHING',
    executor_route_strategy   text              DEFAULT NULL,
    executor_handler          text              DEFAULT NULL,
    executor_param            text              DEFAULT NULL,
    executor_block_strategy   text              DEFAULT NULL,
    executor_timeout          int      NOT NULL DEFAULT '0',
    executor_fail_retry_count int      NOT NULL DEFAULT '0',
    glue_type                 text     NOT NULL,
    glue_source               text,
    glue_remark               text              DEFAULT NULL,
    glue_updatetime           timestamp         DEFAULT NULL,
    child_jobid               text              DEFAULT NULL,
    trigger_status            smallint NOT NULL DEFAULT '0',
    trigger_last_time         bigint   NOT NULL DEFAULT '0',
    trigger_next_time         bigint   NOT NULL DEFAULT '0'
);

COMMENT ON TABLE xxl_job_info IS '存储任务调度信息的表';

COMMENT ON COLUMN xxl_job_info.id IS '主键ID';
COMMENT ON COLUMN xxl_job_info.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_info.job_desc IS '任务描述';
COMMENT ON COLUMN xxl_job_info.add_time IS '创建时间';
COMMENT ON COLUMN xxl_job_info.update_time IS '更新时间';
COMMENT ON COLUMN xxl_job_info.author IS '作者';
COMMENT ON COLUMN xxl_job_info.alarm_email IS '报警邮件';
COMMENT ON COLUMN xxl_job_info.schedule_type IS '调度类型';
COMMENT ON COLUMN xxl_job_info.schedule_conf IS '调度配置';
COMMENT ON COLUMN xxl_job_info.misfire_strategy IS '调度错误策略';
COMMENT ON COLUMN xxl_job_info.executor_route_strategy IS '执行器路由策略';
COMMENT ON COLUMN xxl_job_info.executor_handler IS '任务handler';
COMMENT ON COLUMN xxl_job_info.executor_param IS '任务参数';
COMMENT ON COLUMN xxl_job_info.executor_block_strategy IS '阻塞处理策略';
COMMENT ON COLUMN xxl_job_info.executor_timeout IS '任务执行超时时间';
COMMENT ON COLUMN xxl_job_info.executor_fail_retry_count IS '失败重试次数';
COMMENT ON COLUMN xxl_job_info.glue_type IS 'GLUE类型';
COMMENT ON COLUMN xxl_job_info.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN xxl_job_info.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN xxl_job_info.glue_updatetime IS 'GLUE更新时间';
COMMENT ON COLUMN xxl_job_info.child_jobid IS '子任务ID';
COMMENT ON COLUMN xxl_job_info.trigger_status IS '调度状态';
COMMENT ON COLUMN xxl_job_info.trigger_last_time IS '上次调度时间';
COMMENT ON COLUMN xxl_job_info.trigger_next_time IS '下次调度时间';


CREATE TABLE xxl_job_log
(
    id                        BIGSERIAL PRIMARY KEY,
    job_group                 int      NOT NULL,
    job_id                    int      NOT NULL,
    executor_address          text              DEFAULT NULL,
    executor_handler          text              DEFAULT NULL,
    executor_param            text              DEFAULT NULL,
    executor_sharding_param   text              DEFAULT NULL,
    executor_fail_retry_count int      NOT NULL DEFAULT '0',
    trigger_time              timestamp         DEFAULT NULL,
    trigger_code              int      NOT NULL,
    trigger_msg               text,
    handle_time               timestamp         DEFAULT NULL,
    handle_code               int      NOT NULL,
    handle_msg                text,
    alarm_status              smallint NOT NULL DEFAULT '0'
);

COMMENT ON TABLE xxl_job_log IS '存储任务执行日志的表';

COMMENT ON COLUMN xxl_job_log.id IS '主键ID';
COMMENT ON COLUMN xxl_job_log.job_group IS '执行器主键ID';
COMMENT ON COLUMN xxl_job_log.job_id IS '任务ID';
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


-- 创建 xxl_job_log_report 表
CREATE TABLE xxl_job_log_report
(
    id            SERIAL PRIMARY KEY,
    trigger_day   timestamp,
    running_count integer NOT NULL DEFAULT 0,
    suc_count     integer NOT NULL DEFAULT 0,
    fail_count    integer NOT NULL DEFAULT 0,
    update_time   timestamp
);
COMMENT ON TABLE xxl_job_log_report IS 'XXL-JOB 调度任务执行日志表';
COMMENT ON COLUMN xxl_job_log_report.id IS '自增ID';
COMMENT ON COLUMN xxl_job_log_report.trigger_day IS '调度时间';
COMMENT ON COLUMN xxl_job_log_report.running_count IS '运行中的日志数量';
COMMENT ON COLUMN xxl_job_log_report.suc_count IS '执行成功的日志数量';
COMMENT ON COLUMN xxl_job_log_report.fail_count IS '执行失败的日志数量';
COMMENT ON COLUMN xxl_job_log_report.update_time IS '更新时间';

-- 创建 xxl_job_logglue 表
CREATE TABLE xxl_job_logglue
(
    id          SERIAL PRIMARY KEY,
    job_id      integer NOT NULL,
    glue_type   text,
    glue_source text,
    glue_remark text    NOT NULL,
    add_time    timestamp,
    update_time timestamp
);
COMMENT ON TABLE xxl_job_logglue IS 'XXL-JOB 调度任务 GLUE 代码表';
COMMENT ON COLUMN xxl_job_logglue.id IS '自增ID';
COMMENT ON COLUMN xxl_job_logglue.job_id IS '任务ID';
COMMENT ON COLUMN xxl_job_logglue.glue_type IS 'GLUE 类型';
COMMENT ON COLUMN xxl_job_logglue.glue_source IS 'GLUE 源代码';
COMMENT ON COLUMN xxl_job_logglue.glue_remark IS 'GLUE 备注';
COMMENT ON COLUMN xxl_job_logglue.add_time IS '添加时间';
COMMENT ON COLUMN xxl_job_logglue.update_time IS '更新时间';

-- 创建 xxl_job_registry 表
CREATE TABLE xxl_job_registry
(
    id             SERIAL PRIMARY KEY,
    registry_group text NOT NULL,
    registry_key   text NOT NULL,
    registry_value text NOT NULL,
    update_time    timestamp
);
COMMENT ON TABLE xxl_job_registry IS 'XXL-JOB 执行器注册信息表';
COMMENT ON COLUMN xxl_job_registry.id IS '自增ID';
COMMENT ON COLUMN xxl_job_registry.registry_group IS '注册分组';
COMMENT ON COLUMN xxl_job_registry.registry_key IS '注册键';
COMMENT ON COLUMN xxl_job_registry.registry_value IS '注册值';
COMMENT ON COLUMN xxl_job_registry.update_time IS '更新时间';

-- 创建 xxl_job_group 表
CREATE TABLE xxl_job_group
(
    id           SERIAL PRIMARY KEY,
    app_name     text     NOT NULL,
    title        text     NOT NULL,
    address_type smallint NOT NULL DEFAULT 0,
    address_list text,
    update_time  timestamp
);
COMMENT ON TABLE xxl_job_group IS 'XXL-JOB 执行器注册信息表';
COMMENT ON COLUMN xxl_job_group.id IS '自增ID';
COMMENT ON COLUMN xxl_job_group.app_name IS '执行器 APP 名称';
COMMENT ON COLUMN xxl_job_group.title IS '执行器名称';
COMMENT ON COLUMN xxl_job_group.address_type IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN xxl_job_group.address_list IS '执行器地址列表，多地址逗号分隔';
COMMENT ON COLUMN xxl_job_group.update_time IS '更新时间';

-- 创建 xxl_job_user 表
CREATE TABLE xxl_job_user
(
    id         SERIAL PRIMARY KEY,
    username   text     NOT NULL,
    password   text     NOT NULL,
    role       smallint NOT NULL,
    permission text,
    constraint uk_username unique (username)
);
COMMENT ON TABLE xxl_job_user IS 'XXL-JOB 用户信息表';
COMMENT ON COLUMN xxl_job_user.id IS '自增ID';
COMMENT ON COLUMN xxl_job_user.username IS '账号';
COMMENT ON COLUMN xxl_job_user.password IS '密码';
COMMENT ON COLUMN xxl_job_user.role IS '角色：0-普通用户、1-管理员';
COMMENT ON COLUMN xxl_job_user.permission IS '权限：执行器ID列表，多个逗号分割';

-- 创建 xxl_job_lock 表
CREATE TABLE xxl_job_lock
(
    lock_name text NOT NULL,
    PRIMARY KEY (lock_name)
);
COMMENT ON TABLE xxl_job_lock IS 'XXL-JOB 分布式锁表';
COMMENT ON COLUMN xxl_job_lock.lock_name IS '锁名称';

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time)
VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31');
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type,
                         schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param,
                         executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source,
                         glue_remark, glue_updatetime, child_jobid)
VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *',
        'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化',
        '2018-11-03 22:21:31', '');
INSERT INTO xxl_job_user(id, username, password, role, permission)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock (lock_name)
VALUES ('schedule_lock');
