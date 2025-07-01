# 增加字段
alter table xxl_job_info
    add executor_fail_stop tinyint(1) default 1 null comment '执行失败是否停止' after executor_fail_retry_count;

alter table xxl_job_info
    change alarm_email alarm_url varchar(255) null comment '报警地址';

alter table xxl_job_info
    add alarm_type tinyint(1) default 0 null comment '报警类型(0:不报警;1:邮件;2:企业微信;3:飞书;4:钉钉;5:webhook)' after alarm_url;


alter table xxl_job_log
    add executor_fail_stop tinyint(1) default 1 null comment '执行失败是否停止' after executor_fail_retry_count;

