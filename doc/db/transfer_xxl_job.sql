#
# XXL-JOB v2.3.0
# 脚本迁移数据针对2.0.2-> 2.3版本

SELECT
 CONCAT(
  'INSERT xxl_job_group (`id`,`app_name`, `title`, `address_type`, `address_list`, `update_time`) value('
  ,xjp.id,',',
   CONCAT('''',xjp.app_name,''''),
  ',',
   CONCAT('''',xjp.title,''''),
  ',',
  xjp.address_type,
  ',',
  'null',
  ',',
  'now()' ');'
) AS `-- 执行器迁移`
FROM
 xxl_job_qrtz_trigger_group xjp;

SELECT
 CONCAT(
  'INSERT xxl_job_info(
  `job_group`,
  `job_desc`,
  `add_time`,
  `update_time`,
  `author`,
  `alarm_email`,
  `schedule_type`,
  `schedule_conf`,
  `misfire_strategy`,
  `executor_route_strategy`,
  `executor_handler`,
  `executor_param`,
  `executor_block_strategy`,
  `executor_timeout`,
  `executor_fail_retry_count`,
  `glue_type`,
  `glue_source`,
  `glue_remark`,
  `glue_updatetime`,
  `child_jobid`,
  `trigger_status`,
  `trigger_last_time`,
  `trigger_next_time`) value(',
  xjg.id,','
  ,CONCAT('''',xxj.job_desc,''''),','
  ,CONCAT('''',xxj.add_time,''''),','
  ,CONCAT('''',xxj.update_time,''''),','
  , CONCAT('''',xxj.author,'''') ,','
  , CONCAT('''',xxj.alarm_email,'''') ,','
  ,'''CRON''',','
  ,CONCAT('''',xxj.job_cron,'''') ,','
  , '''DO_NOTHING''' ,','
  ,CONCAT('''',xxj.executor_route_strategy,''''),','
  ,CONCAT('''',xxj.executor_handler,''''),','
  ,CONCAT('''',IFNULL(xxj.executor_param,''),''''),','
  ,CONCAT('''',xxj.executor_block_strategy,''''),','
  ,xxj.executor_timeout,','
  ,xxj.executor_fail_retry_count,','
  ,CONCAT('''',xxj.glue_type,''''),','
  ,CONCAT('''',xxj.glue_source,''''),','
  ,CONCAT('''',xxj.glue_remark,''''),','
  ,CONCAT('''',xxj.glue_updatetime,''''),','
  ,CONCAT('''',xxj.child_jobid,''''),','
  ,'1,0,0',
  ');'
) AS `-- job任务迁移 针对bean type job`
FROM
 XXL_JOB_QRTZ_TRIGGER_INFO xxj  LEFT JOIN xxl_job_qrtz_trigger_group xjg ON xjg.id= xxj.job_group

 WHERE xxj.glue_type = 'BEAN'




