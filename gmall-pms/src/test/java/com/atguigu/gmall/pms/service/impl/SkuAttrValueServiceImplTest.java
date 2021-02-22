package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SaleAttrValueVo;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@SpringBootTest
class SkuAttrValueServiceImplTest {

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Test
    void getAttrValueGroupBySkuIds() {
        List<SaleAttrValueVo> saleAttrValueVos = skuAttrValueService.querySkuValueBySpuId(45l);
        System.out.println(saleAttrValueVos);
    }

    @Test
    void testQueryCurrentSkuAttrValue() {
        Map<Long, String> longStringMap = skuAttrValueService.queryCurrentSkuAttrValue(20l);
        System.out.println(longStringMap);
    }

    @Test
    public void testSkuAttrMapping(){
        String s = skuAttrValueService.querySkuAttrMapping(10l);
        System.out.println(s);
    }

}