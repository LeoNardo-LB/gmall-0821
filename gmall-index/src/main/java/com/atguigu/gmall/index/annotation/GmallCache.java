package com.atguigu.gmall.index.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
// @Component
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {

    /**
     * 前缀
     * @return
     */
    String perfix() default "";

    /**
     * 缓存超时时间
     * @return
     */
    int timeout() default 5;

    /**
     * 缓存随机延长时间，用于防止缓存雪崩
     * @return
     */
    int random() default 5;

    /**
     * 防止缓存击穿的锁
     * @return
     */
    String lockName() default "lock";

}
