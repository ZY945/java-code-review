-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS lottery DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE lottery;

-- 抽奖活动表
CREATE TABLE IF NOT EXISTS `lottery_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `activity_desc` varchar(128) DEFAULT NULL COMMENT '活动描述',
  `begin_date_time` datetime NOT NULL COMMENT '开始时间',
  `end_date_time` datetime NOT NULL COMMENT '结束时间',
  `stock_count` int(11) NOT NULL COMMENT '库存',
  `take_count` int(11) DEFAULT NULL COMMENT '已领取数量',
  `state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '活动状态：0-未开始，1-进行中，2-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽奖活动表';

-- 奖品表
CREATE TABLE IF NOT EXISTS `lottery_prize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `prize_name` varchar(64) NOT NULL COMMENT '奖品名称',
  `prize_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '奖品类型：0-虚拟奖品，1-实物奖品',
  `prize_content` varchar(128) NOT NULL COMMENT '奖品内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖品表';

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户抽奖记录表（核心表，包含唯一索引确保幂等性）
CREATE TABLE IF NOT EXISTS `user_lottery_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `prize_id` bigint(20) NOT NULL COMMENT '奖品ID',
  `message_id` varchar(64) NOT NULL COMMENT '消息ID（用于幂等性控制）',
  `state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态：0-未发放，1-已发放，2-发放失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_activity_prize` (`user_id`, `activity_id`, `prize_id`), -- 业务唯一约束
  UNIQUE KEY `idx_message_id` (`message_id`) -- 消息ID唯一约束，确保幂等性
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户抽奖记录表';
