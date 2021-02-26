package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.service.AsyncCarService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/

@Service
public class AsyncCarServiceImpl implements AsyncCarService {

    @Autowired
    CartMapper cartMapper;

    @Async
    public void modify(String userId, Cart cart) {
        // try {
        //     TimeUnit.SECONDS.sleep(10);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        cartMapper.update(cart, new UpdateWrapper<Cart>().eq("user_id", userId).eq("sku_id", cart.getSkuId()));
    }

    @Async
    public void add(Cart cart) {
        cartMapper.insert(cart);
    }

    @Override
    public Cart query(String userId, String skuId) {
        return cartMapper.selectOne(new QueryWrapper<Cart>().eq("user_id", userId).eq("sku_id", skuId));
    }

    @Override
    public void removeByMap(Map<String, String> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();

        String userId = map.get("userId");
        if (!StringUtils.isBlank(userId)) {
            wrapper.eq("user_id", userId);
            String skuId = map.get("skuId");
            if (!StringUtils.isBlank(skuId)) {
                wrapper.eq("sku_id", skuId);
            }
        }

        if (wrapper.nonEmptyOfWhere()) {
            cartMapper.delete(wrapper);
        }
    }

}
