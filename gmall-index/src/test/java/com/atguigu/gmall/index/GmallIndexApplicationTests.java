package com.atguigu.gmall.index;

import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.index.service.impl.IndexServiceImpl;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GmallIndexApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    IndexService indexService;

    @Autowired
    RBloomFilter bloomFilter;

    @Test
    void contextLoads() {
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent("123", "123");
        System.out.println("aBoolean = " + aBoolean);
    }

    @Test
    void testAspect() {
        System.out.println("indexService = " + indexService);
        indexService.aspectMethod();
    }

    @Test
    public void testBloomFilter(){
    	bloomFilter.add(1);
        bloomFilter.add(2);
        bloomFilter.add(3);
        bloomFilter.add(4);
        bloomFilter.add(5);
        bloomFilter.add(6);

        System.out.println("bloomFilter.contains(1) = " + bloomFilter.contains(1));
        System.out.println("bloomFilter.contains(2) = " + bloomFilter.contains(2));
        System.out.println("bloomFilter.contains(3) = " + bloomFilter.contains(3));
        System.out.println("bloomFilter.contains(4) = " + bloomFilter.contains(4));
        System.out.println("bloomFilter.contains(5) = " + bloomFilter.contains(5));
        System.out.println("bloomFilter.contains(6) = " + bloomFilter.contains(6));
    }

}
