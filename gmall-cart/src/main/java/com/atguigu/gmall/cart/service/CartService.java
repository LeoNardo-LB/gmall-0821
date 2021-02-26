package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
public interface CartService {

    void addCart(Cart cart);

    Cart queryCartBySkuId(Long skuId);

    List<Cart> queryCarts();

    void updateNum(Cart cart);

    void deleteCart(Long skuId);

    void updateRealTimePrice(Long skuId, BigDecimal price);

}
