package com.atguigu.gmall.scheduled.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.dto.Cart;
import com.atguigu.gmall.scheduled.maper.CartPriceSyncMapper;
import com.atguigu.gmall.scheduled.service.CartPriceSyncService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final String REDIS_CART_PREFIX = "cart:info:";

    private static final String REDIS_SKU_PRICE_PREFIX = "cart:price:";

    @Override
    public void syncPrice() {
        // 定时任务
        System.out.println("定时同步任务进行中。。。");
        Set<String> members = stringRedisTemplate.opsForSet().members(PRICE_SYNC_FAILURE_KEY);
        if (StringUtils.isEmpty(members)) return;   // 如果失败集合为空，则直接返回
        members.stream().forEach(userId -> {
            String cartInfoRedisKey = REDIS_CART_PREFIX + userId.toString();
            String currentPriceRedisKey = REDIS_SKU_PRICE_PREFIX;
            // 查询Redis购物车信息
            List<Cart> carts = stringRedisTemplate.opsForHash().entries(cartInfoRedisKey).entrySet().stream()
                                       .map(entry -> {

                                           Cart cart = JSON.parseObject(entry.getValue().toString(), Cart.class);
                                           // 设置实时价格
                                           String currentPrice = stringRedisTemplate.opsForValue().get(currentPriceRedisKey + cart.getSkuId());
                                           cart.setPrice(new BigDecimal(currentPrice));
                                           return cart;
                                       }).collect(Collectors.toList());
            cartPriceSyncMapper.delete(new QueryWrapper<Cart>().eq("user_id", userId));
            carts.stream().forEach(cart -> {
                cartPriceSyncMapper.insert(cart);
            });
        });
        stringRedisTemplate.delete(PRICE_SYNC_FAILURE_KEY);
    }

}
