package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.annotation.TestAnnotation;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    PmsFeignClient pmsFeignClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    final String CACHE_PREFIX = "index:category:";

    @Override
    public List<CategoryEntity> queryLevel0Categories() {
        ResponseVo<List<CategoryEntity>> listResponseVo = pmsFeignClient.queryCategory(0l);
        List<CategoryEntity> categoryEntities = listResponseVo.getData();
        return categoryEntities;
    }

    @Override
    public List<CategoryEntity> queryLevel23Categories(Long pid) {
        String key = CACHE_PREFIX + pid;

        String redisResult = null;
        try {
            redisResult = stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!StringUtils.isBlank(redisResult)) {
            List<CategoryEntity> categoryEntities = JSON.parseArray(redisResult, CategoryEntity.class);
            return categoryEntities;
        }
        List<CategoryEntity> categoryEntities = pmsFeignClient.queryLevel23Categories(pid);
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(categoryEntities));
        return categoryEntities;
    }

    @Override
    @GmallCache(
            perfix = "index:category:cache:",
            lockName = "lock",
            random = 5,
            timeout = 10
    )
    public List<CategoryEntity> queryLevel23CategoriesAnnotation(Long pid) {
        List<CategoryEntity> categoryEntities = pmsFeignClient.queryLevel23Categories(pid);
        return categoryEntities;
    }

    @TestAnnotation
    public void aspectMethod() {
        System.out.println("执行方法");
    }
}
