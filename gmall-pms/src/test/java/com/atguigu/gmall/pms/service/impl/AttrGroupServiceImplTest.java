package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.ItemGroupVo;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@SpringBootTest
class AttrGroupServiceImplTest {

    @Autowired
    AttrGroupService attrGroupService;

    @Test
    void queryAttrGroupBySkuSpuCategory() {
        List<ItemGroupVo> itemGroupVos = attrGroupService.queryAttrGroupBySkuSpuCategory(225l, 15l, 40l);
        System.out.println(itemGroupVos);
    }

}