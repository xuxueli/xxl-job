ALTER TABLE `xxl_job_info`
    ADD COLUMN `schedule_timezone` VARCHAR(64) DEFAULT NULL
        COMMENT 'CRON调度时区，空值使用调度中心默认时区'
        AFTER `schedule_conf`;

-- Rollback:
-- ALTER TABLE `xxl_job_info` DROP COLUMN `schedule_timezone`;
