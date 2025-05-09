package com.dongfeng.springboot.vo;

import lombok.Data;

/**
 * 通用响应对象
 */
@Data
public class Result<T> {
    
    /**
     * 响应码：0-成功，非0-失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     * 
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 失败响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(-1);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
