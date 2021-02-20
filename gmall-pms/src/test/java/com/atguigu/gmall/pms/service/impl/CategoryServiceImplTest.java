package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @Test
    void queryLevel123CategoriesByCid() {
        System.out.println(categoryService.queryLevel123CategoriesByCid(225l));
    }

}