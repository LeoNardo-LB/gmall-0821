package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RBloomFilter bloomFilter;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object gmallCacheAround(ProceedingJoinPoint joinPoint) throws Throwable {

        // 使用布隆过滤器来判断是否有为null
        if (!bloomFilter.contains(Long.parseLong(Arrays.asList(joinPoint.getArgs()).get(0).toString()))) {
            return null;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        String perfix = annotation.perfix();
        Integer timeout = annotation.timeout();
        Integer random = annotation.random();
        String lockname = annotation.lockName();
        Object[] args = joinPoint.getArgs();

        // 查询的key：前缀 + 参数传入的值
        String key = perfix + args[0].toString();

        // 获取超时时间
        int randomTimeout = new Random().nextInt(random);
        Integer totalTimeout = timeout + randomTimeout;

        // 获取返回类型
        Class returnType = signature.getReturnType();

        // 查询缓存中是否已存在，存在则直接返回
        String cacheData = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isBlank(cacheData)) {
            return JSON.parseObject(cacheData, returnType);
        }

        // 否则先加锁，再查询数据库
        RLock lock = redissonClient.getLock(lockname + args[0].toString());
        lock.lock();

        try {

            // 加锁后再次查询，防止已写入
            String cacheData2 = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isBlank(cacheData2)) {
                return JSON.parseObject(cacheData2, returnType);
            }

            // 查询数据库缓存
            Object proceedObj = joinPoint.proceed(args);
            // 解析查询到的结果
            String jsonString = JSON.toJSONString(proceedObj);
            // 存到缓存中
            stringRedisTemplate.opsForValue().set(key, jsonString, totalTimeout, TimeUnit.MINUTES);

            return proceedObj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // 解锁
            lock.unlock();
        }
        return null;
    }

}
