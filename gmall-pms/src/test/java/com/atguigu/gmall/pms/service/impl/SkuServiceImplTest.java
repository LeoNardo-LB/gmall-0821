package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.service.SkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@SpringBootTest
class SkuServiceImplTest {

    @Autowired
    SkuService skuService;

    @Test
    void querySkuImages() {
        List<String> list = skuService.querySkuImages(27l);
        System.out.println("list = " + list);
    }

}