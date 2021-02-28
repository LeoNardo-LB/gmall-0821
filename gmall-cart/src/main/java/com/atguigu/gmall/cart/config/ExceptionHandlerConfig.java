package com.atguigu.gmall.cart.config;

import com.atguigu.gmall.cart.handler.CartAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * Author: Administrator
 * Create: 2021/2/26
 **/
@Configuration
public class ExceptionHandlerConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CartAsyncExceptionHandler();
    }

}
