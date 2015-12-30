/*
Navicat MySQL Data Transfer

Source Server         : meme-127.0.0.1
Source Server Version : 50544
Source Host           : 127.0.0.1:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50544
File Encoding         : 65001

Date: 2015-12-30 23:27:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for qrtz_trigger_log
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_trigger_log`;
CREATE TABLE `qrtz_trigger_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) NOT NULL,
  `job_cron` varchar(128) DEFAULT NULL,
  `job_class` varchar(255) DEFAULT NULL,
  `job_data` varchar(2048) DEFAULT NULL,
  `trigger_time` datetime DEFAULT NULL,
  `trigger_status` varchar(255) DEFAULT NULL,
  `trigger_msg` varchar(255) DEFAULT NULL,
  `handle_time` datetime DEFAULT NULL,
  `handle_status` varchar(255) DEFAULT NULL,
  `handle_msg` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8;
