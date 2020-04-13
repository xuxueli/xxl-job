CREATE TABLE xxl_job_lock (
                              lock_name varchar(50) NOT NULL,
                              PRIMARY KEY (lock_name)
);

INSERT INTO xxl_job_lock ( lock_name) VALUES ( 'schedule_lock');
INSERT INTO kaifa_job_user(id, username, password, role, permission) VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);