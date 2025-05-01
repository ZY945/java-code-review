package com.example.springscaffoldbase.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应包装类
 *
 * @param <T> 返回数据的类型
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码，0表示成功，其他值表示失败
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 带数据的成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含成功状态和数据的结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 不带数据的成功响应
     *
     * @return 包含成功状态的结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 带自定义代码和消息的失败响应
     *
     * @param code    错误代码
     * @param message 错误消息
     * @return 包含失败状态的结果
     */
    public static <T> Result<T> failure(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 带预定义错误代码的失败响应
     *
     * @param resultCode 预定义结果代码
     * @return 包含失败状态的结果
     */
    public static <T> Result<T> failure(ResultCode resultCode) {
        return failure(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 带预定义错误代码和自定义消息的失败响应
     *
     * @param resultCode 预定义结果代码
     * @param message    自定义错误消息
     * @return 包含失败状态的结果
     */
    public static <T> Result<T> failure(ResultCode resultCode, String message) {
        return failure(resultCode.getCode(), message);
    }
}
