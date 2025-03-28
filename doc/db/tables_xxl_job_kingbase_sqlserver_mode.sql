-- 判断数据库是否存在，若不存在则创建
IF NOT EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = N'xxl_job')
    BEGIN
        CREATE DATABASE [xxl_job]
            COLLATE Latin1_General_100_CI_AS_SC_UTF8;
    END
GO

-- 切换到新创建的数据库
USE [xxl_job];
GO

CREATE TABLE xxl_job_info (
                              id INT NOT NULL IDENTITY(1,1),
                              job_group INT NOT NULL,
                              job_desc VARCHAR(255) NOT NULL,
                              add_time DATETIME2 DEFAULT NULL,
                              update_time DATETIME2 DEFAULT NULL,
                              author VARCHAR(64) DEFAULT NULL,
                              alarm_email VARCHAR(255) DEFAULT NULL,
                              schedule_type VARCHAR(50) NOT NULL DEFAULT 'NONE',
                              schedule_conf VARCHAR(128) DEFAULT NULL,
                              misfire_strategy VARCHAR(50) NOT NULL DEFAULT 'DO_NOTHING',
                              executor_route_strategy VARCHAR(50) DEFAULT NULL,
                              executor_handler VARCHAR(255) DEFAULT NULL,
                              executor_param VARCHAR(512) DEFAULT NULL,
                              executor_block_strategy VARCHAR(50) DEFAULT NULL,
                              executor_timeout INT NOT NULL DEFAULT 0,
                              executor_fail_retry_count INT NOT NULL DEFAULT 0,
                              glue_type VARCHAR(50) NOT NULL,
                              glue_source TEXT,
                              glue_remark VARCHAR(128) DEFAULT NULL,
                              glue_updatetime DATETIME2 DEFAULT NULL,
                              child_jobid VARCHAR(255) DEFAULT NULL,
                              trigger_status tinyint NOT NULL DEFAULT 0,
                              trigger_last_time BIGINT NOT NULL DEFAULT 0,
                              trigger_next_time BIGINT NOT NULL DEFAULT 0,
                              PRIMARY KEY (id)
);

CREATE TABLE xxl_job_log (
                             id BIGINT NOT NULL IDENTITY(1,1),
                             job_group INT NOT NULL,
                             job_id INT NOT NULL,
                             executor_address VARCHAR(255) DEFAULT NULL,
                             executor_handler VARCHAR(255) DEFAULT NULL,
                             executor_param VARCHAR(512) DEFAULT NULL,
                             executor_sharding_param VARCHAR(20) DEFAULT NULL,
                             executor_fail_retry_count INT NOT NULL DEFAULT 0,
                             trigger_time DATETIME2 DEFAULT NULL,
                             trigger_code INT NOT NULL,
                             trigger_msg TEXT,
                             handle_time DATETIME2 DEFAULT NULL,
                             handle_code INT NOT NULL,
                             handle_msg TEXT,
                             alarm_status smallint NOT NULL DEFAULT 0,
                             PRIMARY KEY (id)
);

CREATE NONCLUSTERED INDEX I_trigger_time ON xxl_job_log(trigger_time);
CREATE NONCLUSTERED INDEX I_handle_code ON xxl_job_log(handle_code);
CREATE NONCLUSTERED INDEX I_jobid_jobgroup ON xxl_job_log(job_id, job_group);
CREATE NONCLUSTERED INDEX I_job_id ON xxl_job_log(job_id);

CREATE TABLE xxl_job_log_report (
                                    id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
                                    trigger_day DATETIME2 DEFAULT NULL,
                                    running_count INT NOT NULL DEFAULT 0,
                                    suc_count INT NOT NULL DEFAULT 0,
                                    fail_count INT NOT NULL DEFAULT 0,
                                    update_time DATETIME2 DEFAULT NULL,
                                    CONSTRAINT i_trigger_day UNIQUE(trigger_day)
);

CREATE TABLE xxl_job_logglue (
                                 id INT NOT NULL IDENTITY(1,1),
                                 job_id INT NOT NULL,
                                 glue_type VARCHAR(50) DEFAULT NULL,
                                 glue_source TEXT,
                                 glue_remark VARCHAR(128) NOT NULL,
                                 add_time DATETIME2 DEFAULT NULL,
                                 update_time DATETIME2 DEFAULT NULL,
                                 PRIMARY KEY (id)
);

CREATE TABLE xxl_job_registry (
                                  id INT NOT NULL IDENTITY(1,1),
                                  registry_group VARCHAR(50) NOT NULL,
                                  registry_key VARCHAR(255) NOT NULL,
                                  registry_value VARCHAR(255) NOT NULL,
                                  update_time DATETIME2 DEFAULT NULL,
                                  PRIMARY KEY (id)
);
CREATE UNIQUE NONCLUSTERED INDEX i_g_k_v ON xxl_job_registry (registry_group, registry_key, registry_value);


CREATE TABLE xxl_job_group (
                               id INT NOT NULL IDENTITY(1,1),
                               app_name VARCHAR(64) NOT NULL,
                               title VARCHAR(128) NOT NULL,
                               address_type TINYINT NOT NULL DEFAULT 0,
                               address_list TEXT,
                               update_time DATETIME2 DEFAULT NULL,
                               PRIMARY KEY (id)
);

CREATE TABLE xxl_job_user (
                              id INT NOT NULL IDENTITY(1,1),
                              username VARCHAR(50) NOT NULL,
                              password VARCHAR(50) NOT NULL,
                              role TINYINT NOT NULL,
                              permission VARCHAR(255) DEFAULT NULL,
                              PRIMARY KEY (id),
                              CONSTRAINT i_username UNIQUE(username)
);

CREATE TABLE xxl_job_lock (
                              lock_name VARCHAR(50) NOT NULL,
                              PRIMARY KEY (lock_name)
);

INSERT INTO xxl_job_group(app_name, title, address_type, address_list, update_time) VALUES ('xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO xxl_job_info(job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid) VALUES (1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO xxl_job_user(username, password, role, permission) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');











