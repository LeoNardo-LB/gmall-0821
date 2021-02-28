package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.ExceptionHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Author: Administrator
 * Create: 2021/2/26
 **/
@Component
public class ExceptionHandlerServiceImpl implements ExceptionHandlerService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private static final String PRICE_SYNC_FAILURE_KEY = "cart:async:exception";

    @Override
    public void addSyncFailureSet(String userId) {
        stringRedisTemplate.opsForSet().add(PRICE_SYNC_FAILURE_KEY, userId.toString());
    }

}
