package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Author: Administrator
 * Create: 2021/2/20
 **/
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    ItemService itemService;

    @Test
    void itemDetailsPackaging() {
        ItemVo itemVo = itemService.itemDetailsPackaging(7l);
        System.out.println(itemVo);
    }

}