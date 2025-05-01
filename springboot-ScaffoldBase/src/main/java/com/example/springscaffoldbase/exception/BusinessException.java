package com.example.springscaffoldbase.exception;

import com.example.springscaffoldbase.common.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    /**
     * 带错误消息的构造函数
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }
    
    /**
     * 带错误代码和消息的构造函数
     *
     * @param code    错误代码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 带预定义结果代码的构造函数
     *
     * @param resultCode 预定义结果代码
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
    
    /**
     * 带预定义结果代码和自定义消息的构造函数
     *
     * @param resultCode 预定义结果代码
     * @param message    自定义错误消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}
