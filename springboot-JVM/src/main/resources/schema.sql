-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS jvm_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE jvm_test;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建一个用于测试的索引（MySQL不支持IF NOT EXISTS语法创建索引）
-- 使用ALTER TABLE添加索引，如果已存在会报错，可以忽略
ALTER TABLE users ADD INDEX idx_username_email (username, email);
