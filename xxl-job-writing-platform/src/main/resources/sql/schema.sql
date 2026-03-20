-- 写作任务平台数据库表结构

-- 用户表
CREATE TABLE IF NOT EXISTS `wp_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone` varchar(20) NOT NULL COMMENT '手机号',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（加密存储）',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `user_type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型：0-普通用户，1-专家',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '账户状态：0-未激活，1-正常，2-冻结，3-注销',
    `register_time` datetime DEFAULT NULL COMMENT '注册时间',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 专家表
CREATE TABLE IF NOT EXISTS `wp_expert` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专家ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
    `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
    `qualifications` text COMMENT '资质证书信息（JSON格式存储）',
    `expert_fields` text COMMENT '擅长领域（JSON数组格式存储）',
    `introduction` text COMMENT '个人简介',
    `average_score` decimal(3,2) DEFAULT '0.00' COMMENT '历史评分（平均分）',
    `completed_task_count` int NOT NULL DEFAULT '0' COMMENT '总完成任务数',
    `ongoing_task_count` int NOT NULL DEFAULT '0' COMMENT '当前进行中任务数',
    `task_limit` int NOT NULL DEFAULT '5' COMMENT '接单限制（最大同时接单数）',
    `level` tinyint NOT NULL DEFAULT '1' COMMENT '专家等级：1-初级，2-中级，3-高级',
    `certification_status` tinyint NOT NULL DEFAULT '0' COMMENT '认证状态：0-未认证，1-已认证，2-认证中，3-认证失败',
    `certification_time` datetime DEFAULT NULL COMMENT '认证时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_level` (`level`),
    KEY `idx_certification_status` (`certification_status`),
    KEY `idx_average_score` (`average_score`),
    CONSTRAINT `fk_expert_user` FOREIGN KEY (`user_id`) REFERENCES `wp_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家表';

-- 任务表
CREATE TABLE IF NOT EXISTS `wp_task` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `user_id` bigint NOT NULL COMMENT '发布用户ID',
    `title` varchar(100) NOT NULL COMMENT '任务标题',
    `description` text NOT NULL COMMENT '任务描述/详细要求',
    `task_type` tinyint NOT NULL COMMENT '任务类型：1-文章写作，2-文案创作，3-翻译，4-润色修改，5-其他',
    `budget` decimal(10,2) NOT NULL COMMENT '预算价格',
    `actual_price` decimal(10,2) DEFAULT NULL COMMENT '实际成交价格',
    `status` tinyint NOT NULL DEFAULT '10' COMMENT '任务状态：10-已发布，20-已回复，30-已接单，40-已支付，50-进行中，60-已交付，70-已完成，80-已取消',
    `deadline` datetime NOT NULL COMMENT '截止时间',
    `process_description` text COMMENT '流程说明（可复用默认流程）',
    `expected_word_count` int DEFAULT NULL COMMENT '预计完成字数',
    `tags` text COMMENT '领域标签（JSON数组格式存储）',
    `attachments` text COMMENT '附件文件路径（JSON数组格式存储）',
    `expert_id` bigint DEFAULT NULL COMMENT '接单专家ID',
    `accept_time` datetime DEFAULT NULL COMMENT '接单时间',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `deliver_time` datetime DEFAULT NULL COMMENT '交付时间',
    `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
    `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expert_id` (`expert_id`),
    KEY `idx_status` (`status`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_deadline` (`deadline`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_task_user` FOREIGN KEY (`user_id`) REFERENCES `wp_user` (`id`),
    CONSTRAINT `fk_task_expert` FOREIGN KEY (`expert_id`) REFERENCES `wp_expert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 报价表
CREATE TABLE IF NOT EXISTS `wp_quote` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报价ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `expert_id` bigint NOT NULL COMMENT '专家ID',
    `amount` decimal(10,2) NOT NULL COMMENT '报价金额',
    `remark` text COMMENT '报价备注（专家优势、经验等）',
    `estimated_days` int NOT NULL COMMENT '预计完成天数',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '报价状态：0-已报价，1-已选择，2-已拒绝，3-已过期',
    `selected` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否被用户选择',
    `select_time` datetime DEFAULT NULL COMMENT '选择时间',
    `quote_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报价时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_expert` (`task_id`, `expert_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_expert_id` (`expert_id`),
    KEY `idx_status` (`status`),
    KEY `idx_selected` (`selected`),
    CONSTRAINT `fk_quote_task` FOREIGN KEY (`task_id`) REFERENCES `wp_task` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_quote_expert` FOREIGN KEY (`expert_id`) REFERENCES `wp_expert` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报价表';

-- 订单表
CREATE TABLE IF NOT EXISTS `wp_order` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `expert_id` bigint NOT NULL COMMENT '接单专家ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `order_no` varchar(50) NOT NULL COMMENT '订单编号（唯一业务编号）',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '订单状态：1-待支付，2-已支付，3-已取消',
    `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-支付成功，2-支付失败，3-退款中，4-已退款',
    `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `pay_method` tinyint DEFAULT NULL COMMENT '支付方式：1-微信支付，2-支付宝，3-银行卡',
    `pay_transaction_no` varchar(100) DEFAULT NULL COMMENT '支付流水号',
    `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
    `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
    `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
    `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expert_id` (`expert_id`),
    KEY `idx_status` (`status`),
    KEY `idx_pay_status` (`pay_status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_order_task` FOREIGN KEY (`task_id`) REFERENCES `wp_task` (`id`),
    CONSTRAINT `fk_order_expert` FOREIGN KEY (`expert_id`) REFERENCES `wp_expert` (`id`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `wp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 交付表
CREATE TABLE IF NOT EXISTS `wp_delivery` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交付ID',
    `order_id` bigint NOT NULL COMMENT '订单ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `expert_id` bigint NOT NULL COMMENT '专家ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `file_paths` text COMMENT '文件路径（JSON数组格式存储多个文件）',
    `notes` text NOT NULL COMMENT '注意事项',
    `instructions` text NOT NULL COMMENT '使用说明',
    `status` tinyint NOT NULL DEFAULT '0' COMMENT '交付状态：0-已交付，1-已验收通过，2-需要修改，3-已拒绝',
    `user_rating` tinyint DEFAULT NULL COMMENT '用户评分（1-5分）',
    `user_comment` text COMMENT '用户评价',
    `deliver_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交付时间',
    `accept_time` datetime DEFAULT NULL COMMENT '验收时间',
    `modify_requirement` text COMMENT '修改要求',
    `modify_count` int NOT NULL DEFAULT '0' COMMENT '修改次数',
    `final_deliver_time` datetime DEFAULT NULL COMMENT '最终交付时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_expert_id` (`expert_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deliver_time` (`deliver_time`),
    CONSTRAINT `fk_delivery_order` FOREIGN KEY (`order_id`) REFERENCES `wp_order` (`id`),
    CONSTRAINT `fk_delivery_task` FOREIGN KEY (`task_id`) REFERENCES `wp_task` (`id`),
    CONSTRAINT `fk_delivery_expert` FOREIGN KEY (`expert_id`) REFERENCES `wp_expert` (`id`),
    CONSTRAINT `fk_delivery_user` FOREIGN KEY (`user_id`) REFERENCES `wp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交付表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS `wp_notification` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `type` tinyint NOT NULL COMMENT '通知类型：1-系统通知，2-任务通知，3-支付通知，4-交付通知',
    `title` varchar(200) NOT NULL COMMENT '通知标题',
    `content` text NOT NULL COMMENT '通知内容',
    `related_id` bigint DEFAULT NULL COMMENT '关联ID（如任务ID、订单ID等）',
    `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
    `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_read_status` (`read_status`),
    KEY `idx_send_time` (`send_time`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `wp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- 短信发送记录表
CREATE TABLE IF NOT EXISTS `wp_sms_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `phone` varchar(20) NOT NULL COMMENT '手机号',
    `content` text NOT NULL COMMENT '短信内容',
    `type` tinyint NOT NULL COMMENT '短信类型：1-注册验证码，2-登录验证码，3-支付提醒，4-任务提醒，5-系统通知',
    `status` tinyint NOT NULL COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
    `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
    `send_time` datetime DEFAULT NULL COMMENT '发送时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `wp_system_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(100) NOT NULL COMMENT '配置键',
    `config_value` text NOT NULL COMMENT '配置值',
    `config_desc` varchar(500) DEFAULT NULL COMMENT '配置描述',
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 初始化系统配置
INSERT INTO `wp_system_config` (`config_key`, `config_value`, `config_desc`) VALUES
('order_timeout_hours', '24', '订单超时时间（小时）'),
('payment_reminder_minutes', '60', '支付提醒时间（分钟）'),
('deadline_reminder_hours', '12', '截止提醒时间（小时）'),
('max_file_size_mb', '50', '最大文件大小（MB）'),
('allowed_file_types', '.doc,.docx,.pdf,.txt,.md', '允许的文件类型'),
('expert_task_limit_default', '5', '专家默认接单限制'),
('platform_service_fee_rate', '0.10', '平台服务费率（10%）'),
('sms_enabled', 'true', '短信功能是否启用');