package com.atguigu.gmall.index.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient configRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.72.132:6379");
        return Redisson.create(config);
    }

}
