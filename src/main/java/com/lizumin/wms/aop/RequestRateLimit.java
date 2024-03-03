package com.lizumin.wms.aop;

import java.lang.annotation.*;

/**
 * Limit api rate
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestRateLimit {
    long interval() default 10000; // 10s

    int rate() default 10; // 10 times
}
