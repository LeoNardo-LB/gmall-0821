package com.atguigu.gmall.ums.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@Configuration
@Slf4j
public class MqConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate() {
        // 设置数据到达交换机的回调函数
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("数据已到达交换机");
            } else {
                log.warn("数据未到达交换机");
            }
        });

        // 设置数据未到达队列的回调函数
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.warn("数据未到达队列！消息id：{}, 交换机：{}, 路由键：{}, 消息内容：{}"
                    , message.getMessageProperties().getMessageId(), exchange, routingKey, new String(message.getBody()));
        });
    }

}
