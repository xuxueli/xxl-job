-- ----------------------------
-- Table structure for XXL_JOB_GROUP
-- ----------------------------

CREATE TABLE XXL_JOB_GROUP
(
    ID           NUMBER(11, 0)            NOT NULL,
    APP_NAME     VARCHAR2(64)             NOT NULL,
    TITLE        VARCHAR2(12)             NOT NULL,
    ADDRESS_TYPE NUMBER(4, 0) DEFAULT 0 NOT NULL,
    ADDRESS_LIST VARCHAR2(512),
    UPDATE_TIME  DATE         DEFAULT NULL
);
COMMENT ON COLUMN XXL_JOB_GROUP.APP_NAME IS '执行器AppName';
COMMENT ON COLUMN XXL_JOB_GROUP.TITLE IS '执行器名称';
COMMENT ON COLUMN XXL_JOB_GROUP.ADDRESS_TYPE IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN XXL_JOB_GROUP.ADDRESS_LIST IS '执行器地址列表，多地址逗号分隔';

-- ----------------------------
-- Table structure for XXL_JOB_INFO
-- ----------------------------

CREATE TABLE XXL_JOB_INFO
(
    ID                        NUMBER(11, 0)                      NOT NULL,
    JOB_GROUP                 NUMBER(11, 0)                      NOT NULL,
    JOB_DESC                  VARCHAR2(255)                      NOT NULL,
    ADD_TIME                  DATE          DEFAULT NULL,
    UPDATE_TIME               DATE          DEFAULT NULL,
    AUTHOR                    VARCHAR2(64)  DEFAULT NULL,
    ALARM_EMAIL               VARCHAR2(255) DEFAULT NULL,
    SCHEDULE_TYPE             VARCHAR2(50)  DEFAULT 'NONE'       NOT NULL,
    SCHEDULE_CONF             VARCHAR2(128) DEFAULT NULL,
    MISFIRE_STRATEGY          VARCHAR2(50)  DEFAULT 'DO_NOTHING' NOT NULL,
    EXECUTOR_ROUTE_STRATEGY   VARCHAR2(50)  DEFAULT NULL,
    EXECUTOR_HANDLER          VARCHAR2(255) DEFAULT NULL,
    EXECUTOR_PARAM            VARCHAR2(512) DEFAULT NULL,
    EXECUTOR_BLOCK_STRATEGY   VARCHAR2(50)  DEFAULT NULL,
    EXECUTOR_TIMEOUT          NUMBER(11, 0) DEFAULT 0          NOT NULL,
    EXECUTOR_FAIL_RETRY_COUNT NUMBER(11, 0) DEFAULT 0          NOT NULL,
    GLUE_TYPE                 VARCHAR2(50)                       NOT NULL,
    GLUE_SOURCE               NCLOB,
    GLUE_REMARK               VARCHAR2(128) DEFAULT NULL,
    GLUE_UPDATETIME           DATE          DEFAULT NULL,
    CHILD_JOBID               VARCHAR2(255) DEFAULT NULL,
    TRIGGER_STATUS            NUMBER(4, 0)  DEFAULT 0          NOT NULL,
    TRIGGER_LAST_TIME         NUMBER(20, 0) DEFAULT 0          NOT NULL,
    TRIGGER_NEXT_TIME         NUMBER(20, 0) DEFAULT 0          NOT NULL
);

COMMENT ON COLUMN XXL_JOB_INFO.JOB_GROUP IS '执行器主键ID';
COMMENT ON COLUMN XXL_JOB_INFO.AUTHOR IS '作者';
COMMENT ON COLUMN XXL_JOB_INFO.ALARM_EMAIL IS '报警邮件';
COMMENT ON COLUMN XXL_JOB_INFO.SCHEDULE_TYPE IS '调度类型';
COMMENT ON COLUMN XXL_JOB_INFO.SCHEDULE_CONF IS '调度配置，值含义取决于调度类型';
COMMENT ON COLUMN XXL_JOB_INFO.MISFIRE_STRATEGY IS '调度过期策略';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_ROUTE_STRATEGY IS '执行器路由策略';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_HANDLER IS '执行器任务handler';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_PARAM IS '执行器任务参数';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_BLOCK_STRATEGY IS '阻塞处理策略';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_TIMEOUT IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN XXL_JOB_INFO.EXECUTOR_FAIL_RETRY_COUNT IS '失败重试次数';
COMMENT ON COLUMN XXL_JOB_INFO.GLUE_TYPE IS 'GLUE类型';
COMMENT ON COLUMN XXL_JOB_INFO.GLUE_SOURCE IS 'GLUE源代码';
COMMENT ON COLUMN XXL_JOB_INFO.GLUE_REMARK IS 'GLUE备注';
COMMENT ON COLUMN XXL_JOB_INFO.GLUE_UPDATETIME IS 'GLUE更新时间';
COMMENT ON COLUMN XXL_JOB_INFO.CHILD_JOBID IS '子任务ID，多个逗号分隔';
COMMENT ON COLUMN XXL_JOB_INFO.TRIGGER_STATUS IS '调度状态：0-停止，1-运行';
COMMENT ON COLUMN XXL_JOB_INFO.TRIGGER_LAST_TIME IS '上次调度时间';
COMMENT ON COLUMN XXL_JOB_INFO.TRIGGER_NEXT_TIME IS '下次调度时间';

-- ----------------------------
-- Table structure for XXL_JOB_LOCK
-- ----------------------------

CREATE TABLE XXL_JOB_LOCK
(
    LOCK_NAME VARCHAR2(50) NOT NULL
);

COMMENT ON COLUMN XXL_JOB_LOCK.LOCK_NAME IS '锁名称';

-- ----------------------------
-- Table structure for XXL_JOB_LOG
-- ----------------------------

CREATE TABLE XXL_JOB_LOG
(
    ID                        NUMBER(20, 0)             NOT NULL,
    JOB_GROUP                 NUMBER(11, 0)             NOT NULL,
    JOB_ID                    NUMBER(11, 0)             NOT NULL,
    EXECUTOR_ADDRESS          VARCHAR2(255) DEFAULT NULL,
    EXECUTOR_HANDLER          VARCHAR2(255) DEFAULT NULL,
    EXECUTOR_PARAM            VARCHAR2(512) DEFAULT NULL,
    EXECUTOR_SHARDING_PARAM   VARCHAR2(20)  DEFAULT NULL,
    EXECUTOR_FAIL_RETRY_COUNT NUMBER(11, 0) DEFAULT 0   NOT NULL,
    TRIGGER_TIME              DATE          DEFAULT NULL,
    TRIGGER_CODE              NUMBER(11, 0)             NOT NULL,
    TRIGGER_MSG               NCLOB,
    HANDLE_TIME               DATE          DEFAULT NULL,
    HANDLE_CODE               NUMBER(11, 0)             NOT NULL,
    HANDLE_MSG                NCLOB,
    ALARM_STATUS              NUMBER(4, 0)  DEFAULT 0 NOT NULL
);

COMMENT ON COLUMN XXL_JOB_LOG.JOB_GROUP IS '执行器主键ID';
COMMENT ON COLUMN XXL_JOB_LOG.JOB_ID IS '任务，主键ID';
COMMENT ON COLUMN XXL_JOB_LOG.EXECUTOR_ADDRESS IS '执行器地址，本次执行的地址';
COMMENT ON COLUMN XXL_JOB_LOG.EXECUTOR_HANDLER IS '执行器任务handler';
COMMENT ON COLUMN XXL_JOB_LOG.EXECUTOR_PARAM IS '执行器任务参数';
COMMENT ON COLUMN XXL_JOB_LOG.EXECUTOR_SHARDING_PARAM IS '执行器任务分片参数，格式如 1/2';
COMMENT ON COLUMN XXL_JOB_LOG.EXECUTOR_FAIL_RETRY_COUNT IS '失败重试次数';
COMMENT ON COLUMN XXL_JOB_LOG.TRIGGER_TIME IS '调度-时间';
COMMENT ON COLUMN XXL_JOB_LOG.TRIGGER_CODE IS '调度-结果';
COMMENT ON COLUMN XXL_JOB_LOG.TRIGGER_MSG IS '调度-日志';
COMMENT ON COLUMN XXL_JOB_LOG.HANDLE_TIME IS '执行-时间';
COMMENT ON COLUMN XXL_JOB_LOG.HANDLE_CODE IS '执行-状态';
COMMENT ON COLUMN XXL_JOB_LOG.HANDLE_MSG IS '执行-日志';
COMMENT ON COLUMN XXL_JOB_LOG.ALARM_STATUS IS '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';

-- ----------------------------
-- Table structure for XXL_JOB_LOG_REPORT
-- ----------------------------

CREATE TABLE XXL_JOB_LOG_REPORT
(
    ID            NUMBER(11, 0) NOT NULL,
    TRIGGER_DAY   DATE DEFAULT NULL,
    RUNNING_COUNT NUMBER(11, 0) DEFAULT 0 NOT NULL,
    SUC_COUNT     NUMBER(11, 0) DEFAULT 0 NOT NULL,
    FAIL_COUNT    NUMBER(11, 0) DEFAULT 0 NOT NULL,
    UPDATE_TIME   DATE DEFAULT NULL
);

COMMENT ON COLUMN XXL_JOB_LOG_REPORT.TRIGGER_DAY IS '调度-时间';
COMMENT ON COLUMN XXL_JOB_LOG_REPORT.RUNNING_COUNT IS '运行中-日志数量';
COMMENT ON COLUMN XXL_JOB_LOG_REPORT.SUC_COUNT IS '执行成功-日志数量';
COMMENT ON COLUMN XXL_JOB_LOG_REPORT.FAIL_COUNT IS '执行失败-日志数量';

-- ----------------------------
-- Table structure for XXL_JOB_LOGGLUE
-- ----------------------------

CREATE TABLE XXL_JOB_LOGGLUE
(
    ID          NUMBER(11, 0) NOT NULL,
    JOB_ID      NUMBER(11, 0) NOT NULL,
    GLUE_TYPE   VARCHAR2(50) DEFAULT NULL,
    GLUE_SOURCE NCLOB,
    GLUE_REMARK VARCHAR2(128) NOT NULL,
    ADD_TIME    DATE DEFAULT NULL,
    UPDATE_TIME DATE DEFAULT NULL
);

COMMENT ON COLUMN XXL_JOB_LOGGLUE.JOB_ID IS '任务，主键ID';
COMMENT ON COLUMN XXL_JOB_LOGGLUE.GLUE_TYPE IS 'GLUE类型';
COMMENT ON COLUMN XXL_JOB_LOGGLUE.GLUE_SOURCE IS 'GLUE源代码';
COMMENT ON COLUMN XXL_JOB_LOGGLUE.GLUE_REMARK IS 'GLUE备注';

-- ----------------------------
-- Table structure for XXL_JOB_REGISTRY
-- ----------------------------

CREATE TABLE XXL_JOB_REGISTRY
(
    ID             NUMBER(11, 0) NOT NULL,
    REGISTRY_GROUP VARCHAR2(50)  NOT NULL,
    REGISTRY_KEY   VARCHAR2(255) NOT NULL,
    REGISTRY_VALUE VARCHAR2(255) NOT NULL,
    UPDATE_TIME    DATE
);

-- ----------------------------
-- Table structure for XXL_JOB_USER
-- ----------------------------

CREATE TABLE XXL_JOB_USER
(
    ID         NUMBER(11, 0) NOT NULL,
    USERNAME   VARCHAR2(50)  NOT NULL,
    PASSWORD   VARCHAR2(50)  NOT NULL,
    ROLE       NUMBER(4, 0)  NOT NULL,
    PERMISSION VARCHAR2(255)
);

COMMENT ON COLUMN XXL_JOB_USER.USERNAME IS '账号';
COMMENT ON COLUMN XXL_JOB_USER.PASSWORD IS '密码';
COMMENT ON COLUMN XXL_JOB_USER.ROLE IS '角色：0-普通用户、1-管理员';
COMMENT ON COLUMN XXL_JOB_USER.PERMISSION IS '权限：执行器ID列表，多个逗号分割';

-- ----------------------------
-- Primary Key structure for table XXL_JOB_GROUP
-- ----------------------------
ALTER TABLE XXL_JOB_GROUP
    ADD CONSTRAINT PK_JOB_GROUP PRIMARY KEY (ID);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_INFO
-- ----------------------------
ALTER TABLE XXL_JOB_INFO
    ADD CONSTRAINT PK_JOB_INFO PRIMARY KEY (ID);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_LOCK
-- ----------------------------
ALTER TABLE XXL_JOB_LOCK
    ADD CONSTRAINT PK_JOB_LOCK PRIMARY KEY (LOCK_NAME);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_LOG
-- ----------------------------
ALTER TABLE XXL_JOB_LOG
    ADD CONSTRAINT PK_JOB_LOG PRIMARY KEY (ID);

CREATE INDEX I_handle_code
    ON XXL_JOB_LOG (HANDLE_CODE ASC);

CREATE INDEX "I_trigger_time"
    ON XXL_JOB_LOG (TRIGGER_TIME ASC);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_LOG_REPORT
-- ----------------------------
ALTER TABLE XXL_JOB_LOG_REPORT
    ADD CONSTRAINT PK_JOB_LOG_REPORT PRIMARY KEY (ID);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_LOGGLUE
-- ----------------------------
ALTER TABLE XXL_JOB_LOGGLUE
    ADD CONSTRAINT PK_JOB_LOGGLUE PRIMARY KEY (ID);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_REGISTRY
-- ----------------------------
ALTER TABLE XXL_JOB_REGISTRY
    ADD CONSTRAINT PK_JOB_REGISTRY PRIMARY KEY (ID);

-- ----------------------------
-- Primary Key structure for table XXL_JOB_USER
-- ----------------------------
ALTER TABLE XXL_JOB_USER
    ADD CONSTRAINT PK_JOB_USER PRIMARY KEY (ID);

-- Create sequence
CREATE SEQUENCE XXL_JOB_GROUP_ID START WITH 2 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_INFO_ID START WITH 2 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_LOGGLUE_ID START WITH 1 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_LOG_ID START WITH 1 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_REGISTRY_ID START WITH 1 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_USER_ID START WITH 2 INCREMENT BY 1 CACHE 20;
CREATE SEQUENCE XXL_JOB_LOG_REPORT_ID START WITH 2 INCREMENT BY 1 CACHE 20;

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time) VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, sysdate );
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid) VALUES (1, 1, '测试任务1', sysdate, sysdate, 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', sysdate, '');
INSERT INTO xxl_job_user(id, username, password, role, permission) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
INSERT INTO xxl_job_lock(lock_name) VALUES ('schedule_lock');