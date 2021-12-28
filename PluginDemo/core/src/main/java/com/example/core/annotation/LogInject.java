package com.example.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author heyufei
 * @version 1.0
 * @className HycanLogInject
 * @description XLog插桩，保留在字节码
 * @since 2021/12/17 10:06 上午
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LogInject {
    enum LOG_TYPE {
        //debug级别
        DEBUG,
        //info级别
        INFO,
        //error级别
        ERROR,
        //warn级别
        WARN;

    }

    LOG_TYPE logType() default LOG_TYPE.DEBUG;

    String msg();
}
