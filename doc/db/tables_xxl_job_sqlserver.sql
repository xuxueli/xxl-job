CREATE TABLE xxl_job_info (
                              id int NOT NULL identity(1,1),
                              job_group int NOT NULL,
                              job_desc varchar(255) NOT NULL,
                              add_time datetime2 DEFAULT NULL,
                              update_time datetime2 DEFAULT NULL,
                              author varchar(64) DEFAULT NULL ,
                              alarm_email varchar(255) DEFAULT NULL ,
                              schedule_type varchar(50) NOT NULL DEFAULT 'NONE',
                              schedule_conf varchar(128) DEFAULT NULL ,
                              misfire_strategy varchar(50) NOT NULL DEFAULT 'DO_NOTHING' ,
                              executor_route_strategy varchar(50) DEFAULT NULL ,
                              executor_handler varchar(255) DEFAULT NULL ,
                              executor_param varchar(512) DEFAULT NULL ,
                              executor_block_strategy varchar(50) DEFAULT NULL ,
                              executor_timeout int NOT NULL DEFAULT '0',
                              executor_fail_retry_count int NOT NULL DEFAULT '0',
                              glue_type varchar(50) NOT NULL ,
                              glue_source varchar(512) ,
                              glue_remark varchar(128) DEFAULT NULL ,
                              glue_updatetime datetime2 DEFAULT NULL ,
                              child_jobid varchar(255) DEFAULT NULL ,
                              trigger_status tinyint NOT NULL DEFAULT '0',
                              trigger_last_time bigint NOT NULL DEFAULT '0' ,
                              trigger_next_time bigint NOT NULL DEFAULT '0',
                              PRIMARY KEY (id)
);

CREATE TABLE xxl_job_log (
                             id bigint NOT NULL identity(1,1),
                             job_group int NOT NULL,
                             job_id int NOT NULL ,
                             executor_address varchar(255) DEFAULT NULL ,
                             executor_handler varchar(255) DEFAULT NULL,
                             executor_param varchar(512) DEFAULT NULL,
                             executor_sharding_param varchar(20) DEFAULT NULL,
                             executor_fail_retry_count int NOT NULL DEFAULT '0',
                             trigger_time datetime2 DEFAULT NULL,
                             trigger_code int NOT NULL,
                             trigger_msg varchar(512),
                             handle_time datetime2 DEFAULT NULL,
                             handle_code int NOT NULL ,
                             handle_msg varchar(512),
                             alarm_status tinyint NOT NULL DEFAULT '0',
                             PRIMARY KEY (id)
);

CREATE NONCLUSTERED INDEX I_trigger_time ON xxl_job_log(trigger_time);
CREATE NONCLUSTERED INDEX I_handle_code ON xxl_job_log(handle_code);

CREATE TABLE xxl_job_log_report (
                                    id int NOT NULL identity(1,1) PRIMARY KEY ,
                                    trigger_day datetime2 DEFAULT NULL,
                                    running_count int NOT NULL DEFAULT '0',
                                    suc_count int NOT NULL DEFAULT '0',
                                    fail_count int NOT NULL DEFAULT '0',
                                    update_time datetime2 DEFAULT NULL,
                                    constraint i_trigger_day unique(trigger_day)
);

CREATE TABLE xxl_job_logglue (
                                 id int NOT NULL identity(1,1),
                                 job_id int NOT NULL,
                                 glue_type varchar(50) DEFAULT NULL,
                                 glue_source varchar(512),
                                 glue_remark varchar(128) NOT NULL,
                                 add_time datetime2 DEFAULT NULL,
                                 update_time datetime2 DEFAULT NULL,
                                 PRIMARY KEY (id)
);

CREATE TABLE xxl_job_registry (
                                  id int NOT NULL identity(1,1),
                                  registry_group varchar(50) NOT NULL,
                                  registry_key varchar(255) NOT NULL,
                                  registry_value varchar(255) NOT NULL,
                                  update_time datetime2 DEFAULT NULL,
                                  PRIMARY KEY (id)
);
CREATE NONCLUSTERED INDEX i_g_k_v ON xxl_job_registry(registry_group, registry_key, registry_value);

CREATE TABLE xxl_job_group (
                               id int NOT NULL identity(1,1),
                               app_name varchar(64) NOT NULL,
                               title varchar(128) NOT NULL,
                               address_type smallint NOT NULL DEFAULT '0',
                               address_list varchar(512),
                               update_time datetime2 DEFAULT NULL,
                               PRIMARY KEY (id)
);

CREATE TABLE xxl_job_user (
                              id int NOT NULL identity(1,1),
                              username varchar(50) NOT NULL,
                              password varchar(50) NOT NULL,
                              role smallint NOT NULL,
                              permission varchar(255) DEFAULT NULL,
                              PRIMARY KEY (id),
                              constraint i_username unique(username)
);

CREATE TABLE xxl_job_lock (
                              lock_name varchar(50) NOT NULL,
                              PRIMARY KEY (lock_name)
);

INSERT INTO xxl_job_group(app_name, title, address_type, address_list, update_time) VALUES ('xxl-job-executor-sample', '示例执行器', 0, NULL, '2018-11-03 22:21:31' );
INSERT INTO xxl_job_info(job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid) VALUES (1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '');
INSERT INTO xxl_job_user(username, password, role, permission) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');