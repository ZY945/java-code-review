-- 创建数据库
CREATE
DATABASE IF NOT EXISTS coupon_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE
coupon_db;

-- 创建优惠券模板表
CREATE TABLE IF NOT EXISTS c_coupon_template
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT
    COMMENT
    '主键',
    name
    VARCHAR
(
    64
) NOT NULL COMMENT '优惠券名称',
    description VARCHAR
(
    256
) COMMENT '描述',
    type INT NOT NULL COMMENT '券类型:1-满减券,2-折扣券,3-立减券',
    discount DECIMAL
(
    10,
    2
) NOT NULL COMMENT '优惠金额或折扣率',
    threshold DECIMAL
(
    10,
    2
) COMMENT '使用门槛金额',
    total INT NOT NULL COMMENT '发行数量',
    remaining INT NOT NULL COMMENT '剩余数量',
    start_time DATETIME NOT NULL COMMENT '有效期开始时间',
    end_time DATETIME NOT NULL COMMENT '有效期结束时间',
    status INT NOT NULL DEFAULT 1 COMMENT '状态:1-未开始,2-进行中,3-已结束,4-已关闭',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    INDEX idx_status
(
    status
),
    INDEX idx_start_time
(
    start_time
),
    INDEX idx_end_time
(
    end_time
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 创建用户优惠券表
CREATE TABLE IF NOT EXISTS c_user_coupon
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT
    COMMENT
    '主键',
    user_id
    BIGINT
    NOT
    NULL
    COMMENT
    '用户ID',
    template_id
    BIGINT
    NOT
    NULL
    COMMENT
    '优惠券模板ID',
    status
    INT
    NOT
    NULL
    DEFAULT
    1
    COMMENT
    '状态:1-未使用,2-已使用,3-已过期',
    order_id
    BIGINT
    COMMENT
    '使用的订单ID',
    create_time
    DATETIME
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP
    COMMENT
    '创建时间',
    update_time
    DATETIME
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP
    ON
    UPDATE
    CURRENT_TIMESTAMP
    COMMENT
    '更新时间',
    version
    INT
    NOT
    NULL
    DEFAULT
    0
    COMMENT
    '版本号',
    INDEX
    idx_user_id
(
    user_id
),
    INDEX idx_template_id
(
    template_id
),
    INDEX idx_status
(
    status
),
    INDEX idx_order_id
(
    order_id
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 创建用户并授权
CREATE
USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON coupon_db.* TO
'root'@'localhost';
FLUSH
PRIVILEGES;

-- 修改密码认证方式
ALTER
USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password';
FLUSH
PRIVILEGES;

-- 插入测试数据
INSERT INTO c_coupon_template (name, description, type, discount, threshold,
                               total, remaining, start_time, end_time, status)
VALUES ('满100减50', '双11优惠券', 1, 50.00, 100.00,
        1000, 1000, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 2);

INSERT INTO c_coupon_template (name, description, type, discount, threshold,
                               total, remaining, start_time, end_time, status)
VALUES ('9折优惠', '新年优惠券', 2, 0.90, 0.00,
        500, 500, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 2);

INSERT INTO c_coupon_template (name, description, type, discount, threshold,
                               total, remaining, start_time, end_time, status)
VALUES ('立减10元', '新人优惠券', 3, 10.00, 0.00,
        2000, 2000, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 2);