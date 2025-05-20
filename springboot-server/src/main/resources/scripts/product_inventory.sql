-- 创建商品库存表
CREATE TABLE IF NOT EXISTS `product_inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存数量',
  `locked_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存数量（已下单但未支付）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号（用于乐观锁）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_product_id` (`product_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存表';

-- 插入测试数据
INSERT INTO `product_inventory` (`product_id`, `product_name`, `stock`, `locked_stock`, `version`)
VALUES
(1001, 'iPhone 14 Pro Max', 100, 0, 1),
(1002, 'MacBook Pro 16', 50, 0, 1),
(1003, 'iPad Pro 12.9', 200, 0, 1),
(1004, 'AirPods Pro', 300, 0, 1),
(1005, 'Apple Watch Series 8', 150, 0, 1);
