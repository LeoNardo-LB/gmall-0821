package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
public interface CartService {

    List<Cart> queryCheckedCart(String userId);

    void addCart(Cart cart);

    Cart queryCartBySkuId(Long skuId);

    List<Cart> queryCarts();

    void updateNum(Cart cart);

    void deleteCart(Long skuId);

    void updateRealTimePrice(Long skuId, BigDecimal price);

    void updateCheckStatus(String skuId, Boolean check);

}
