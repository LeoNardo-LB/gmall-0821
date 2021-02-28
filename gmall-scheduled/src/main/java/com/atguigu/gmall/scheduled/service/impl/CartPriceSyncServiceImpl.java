package com.atguigu.gmall.scheduled.service.impl;

import com.atguigu.gmall.scheduled.maper.CartPriceSyncMapper;
import com.atguigu.gmall.scheduled.service.CartPriceSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Author: Administrator
 * Create: 2021/2/27
 **/
@Service
public class CartPriceSyncServiceImpl implements CartPriceSyncService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    CartPriceSyncMapper cartPriceSyncMapper;

    private static final String PRICE_SYNC_FAILURE_KEY = "cart:async:exception";

    @Override
    public boolean syncPrice() {
        // 定时任务
        System.out.println("定时同步任务进行中。。。");
        // Set<String> members = stringRedisTemplate.opsForSet().members(PRICE_SYNC_FAILURE_KEY);
        // members.iterator().forEachRemaining(userId -> {
        //     BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(userId);
        //     Set<Object> keys = hashOps.keys();
        // });
        return false;
    }

}
