package com.example.springscaffoldbase.common;

import lombok.Getter;

/**
 * API响应的预定义结果代码
 */
@Getter
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR(5000, "系统错误，请联系管理员"),
    
    /**
     * 参数验证错误
     */
    PARAM_ERROR(4000, "参数验证错误"),
    
    /**
     * 业务逻辑错误
     */
    BUSINESS_ERROR(4100, "业务处理失败"),
    
    /**
     * 认证错误
     */
    UNAUTHORIZED(4010, "未授权，请先登录"),
    
    /**
     * 权限不足
     */
    FORBIDDEN(4030, "权限不足，无法访问"),
    
    /**
     * 资源未找到
     */
    NOT_FOUND(4040, "请求的资源不存在"),
    
    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(4050, "请求方法不允许"),
    
    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(4080, "请求超时，请稍后重试"),
    
    /**
     * 请求过多
     */
    TOO_MANY_REQUESTS(4290, "请求过于频繁，请稍后重试"),
    
    /**
     * 用户相关错误
     */
    USER_NOT_FOUND(4001, "用户不存在"),
    USER_ALREADY_EXISTS(4002, "用户已存在"),
    USER_PASSWORD_ERROR(4003, "用户名或密码错误"),
    USER_ACCOUNT_LOCKED(4004, "账号已被锁定"),
    USER_ACCOUNT_DISABLED(4005, "账号已被禁用"),
    
    /**
     * 数据相关错误
     */
    DATA_ALREADY_EXISTS(4101, "数据已存在"),
    DATA_NOT_FOUND(4102, "数据不存在"),
    DATA_CONSTRAINT_ERROR(4103, "数据约束错误"),
    DATA_INTEGRITY_ERROR(4104, "数据完整性错误"),
    
    /**
     * 文件相关错误
     */
    FILE_UPLOAD_ERROR(4201, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(4202, "文件下载失败"),
    FILE_SIZE_LIMIT(4203, "文件大小超出限制"),
    FILE_TYPE_ERROR(4204, "文件类型不支持"),
    
    /**
     * 第三方服务错误
     */
    THIRD_PARTY_SERVICE_ERROR(5100, "第三方服务调用失败"),
    REMOTE_SERVICE_ERROR(5200, "远程服务调用失败");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
