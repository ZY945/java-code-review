package com.dongfeng.springbootmvc.common.idempotency;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>接口幂等性注解</b>
 */
@Target(ElementType.METHOD) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface ApiIdempotent {

    /**
     * 幂等性token
     */
    String value() default "";

    /**
     * 幂等性key的前缀
     */
    String prefix() default "";

    /**
     * 幂等性返回描述
     */
    String message() default "请勿重复提交";
}
