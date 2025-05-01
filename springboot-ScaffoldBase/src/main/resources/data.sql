-- 使用数据库
USE scaffold_dev;

-- 插入初始用户数据（密码是加密后的 'admin123'）
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `status`)
VALUES 
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin@example.com', '13800000000', 1)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 插入角色数据
INSERT INTO `role` (`name`, `code`, `description`)
VALUES 
('系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('普通用户', 'ROLE_USER', '普通用户，拥有基本权限')
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 关联用户和角色
INSERT INTO `user_role` (`user_id`, `role_id`)
SELECT 
    (SELECT `id` FROM `user` WHERE `username` = 'admin'),
    (SELECT `id` FROM `role` WHERE `code` = 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE `create_time` = CURRENT_TIMESTAMP;

-- 插入权限数据
INSERT INTO `permission` (`name`, `code`, `type`, `parent_id`, `path`, `icon`, `sort`)
VALUES 
-- 菜单
('系统管理', 'system:manage', 1, NULL, '/system', 'setting', 1),
('用户管理', 'system:user', 1, 1, '/system/user', 'user', 1),
('角色管理', 'system:role', 1, 1, '/system/role', 'team', 2),
('权限管理', 'system:permission', 1, 1, '/system/permission', 'safety', 3),
('系统设置', 'system:config', 1, 1, '/system/config', 'tool', 4),

-- 按钮
('用户新增', 'system:user:add', 2, 2, NULL, NULL, 1),
('用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 2),
('用户删除', 'system:user:delete', 2, 2, NULL, NULL, 3),
('角色新增', 'system:role:add', 2, 3, NULL, NULL, 1),
('角色编辑', 'system:role:edit', 2, 3, NULL, NULL, 2),
('角色删除', 'system:role:delete', 2, 3, NULL, NULL, 3),
('权限新增', 'system:permission:add', 2, 4, NULL, NULL, 1),
('权限编辑', 'system:permission:edit', 2, 4, NULL, NULL, 2),
('权限删除', 'system:permission:delete', 2, 4, NULL, NULL, 3)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 关联角色和权限（管理员角色拥有所有权限）
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    (SELECT `id` FROM `role` WHERE `code` = 'ROLE_ADMIN'),
    `id`
FROM `permission`
ON DUPLICATE KEY UPDATE `create_time` = CURRENT_TIMESTAMP;

-- 关联普通用户角色和基本权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 
    (SELECT `id` FROM `role` WHERE `code` = 'ROLE_USER'),
    `id`
FROM `permission` 
WHERE `code` IN ('system:user', 'system:config')
ON DUPLICATE KEY UPDATE `create_time` = CURRENT_TIMESTAMP;

-- 系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`)
VALUES 
('system.name', 'Spring脚手架系统', '系统名称'),
('system.version', '1.0.0', '系统版本'),
('system.logo', '/static/logo.png', '系统Logo'),
('system.copyright', 'Copyright © 2025 Example Inc.', '版权信息')
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;
