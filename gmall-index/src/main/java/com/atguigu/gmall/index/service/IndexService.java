package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
public interface IndexService {

    List<CategoryEntity> queryLevel0Categories();

    List<CategoryEntity> queryLevel23Categories(Long pid);

    List<CategoryEntity> queryLevel23CategoriesAnnotation(Long pid);

    void aspectMethod();

}
