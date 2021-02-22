package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@SpringBootTest
class SkuBoundsServiceImplTest {

    @Autowired
    SkuBoundsService skuBoundsService;

    @Test
    void getSaleStrategyBySkuId() {
        List<ItemSaleVo> saleStrategyBySkuId = skuBoundsService.getSaleStrategyBySkuId(3l);
        System.out.println(saleStrategyBySkuId);
    }

}