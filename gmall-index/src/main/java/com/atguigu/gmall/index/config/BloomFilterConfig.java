package com.atguigu.gmall.index.config;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.PmsFeignClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import jodd.util.CollectionUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/15
 **/
@Configuration
public class BloomFilterConfig {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    PmsFeignClient pmsFeignClient;

    @Bean
    public RBloomFilter rBloomFilter() {
        RBloomFilter<Object> bloomfilter = redissonClient.getBloomFilter("bloomfilter");
        // 初始化布隆过滤器
        bloomfilter.tryInit(50l, 0.03);

        ResponseVo<List<CategoryEntity>> listResponseVo = pmsFeignClient.queryCategory(0l);
        List<CategoryEntity> data = listResponseVo.getData();
        if (!CollectionUtils.isEmpty(data)) {
            data.forEach(item -> {
                bloomfilter.add(item.getId());
            });
        }
        return bloomfilter;
    }

}
