package com.dongfeng.springbootmvc.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<T>()
                .setCode(200)
                .setMessage("success");
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(200)
                .setMessage("success")
                .setData(data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<T>()
                .setCode(500)
                .setMessage(message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message);
    }
} 