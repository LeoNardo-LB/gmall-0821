package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.Cart;

import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
public interface AsyncCarService {

    public void modify(String userId, Cart cart);

    public void add(Cart cart);

    Cart query(String userId, String skuId);

    void removeByMap(Map<String, String> map);

    void updateCheckStatus(Cart cart);

}
