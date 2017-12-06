create table if not exists `{job_table}` (
  `id` int(11) not null auto_increment,
  `name` varchar(255) default null,
  `status` int(11) default null,
  `price` double(10,4) default null,
  `flag` varchar(5) default null,
  `total` decimal(20,0) default null,
  `priority` int(11) comment '优先级,(数值越大,优先级越低)',
  `code` varchar(32) comment '消息编码',
  `msg` text comment '消息',
  `retry_times` int(11) comment '重试次数',
  `max_retry_times` int(11) default '0' comment '最大重试次数',
  `content` text default null,
  `updatetime` datetime default null,
  `createtime` datetime default current_timestamp(),
  primary key (`id`)
) engine=InnoDB default charset=utf8;