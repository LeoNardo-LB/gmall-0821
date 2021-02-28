package com.atguigu.gmall.cart.handler;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.service.ExceptionHandlerService;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Author: Administrator
 * Create: 2021/2/26
 **/
@Component
public class CartAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Autowired
    ExceptionHandlerService exceptionHandlerService;

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        if (objects != null && objects.length > 0) {
            String userId = null;
            if (objects[0] instanceof Cart) {
                // 这个对象同步失败，加入Redis同步失败名单
                Cart cart = (Cart) objects[0];
                userId = cart.getUserId();
                exceptionHandlerService.addSyncFailureSet(userId);
            }
        }
    }

}
