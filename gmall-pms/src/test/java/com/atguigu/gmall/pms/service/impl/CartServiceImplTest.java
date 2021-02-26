package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
@SpringBootTest
class CartServiceImplTest {

    @Autowired
    CartService cartService;

    @Test
    void updateRealTimePrice() {
        cartService.updateRealTimePrice(10l, new BigDecimal("5001"));
    }

}