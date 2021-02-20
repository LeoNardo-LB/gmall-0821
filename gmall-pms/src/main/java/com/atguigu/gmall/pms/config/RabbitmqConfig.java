package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
@Configuration
@Slf4j
public class RabbitmqConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息已达到交换机。");
            } else {
                log.warn("消息未达到交换机！");
            }
        });

        rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
            log.warn("消息未达到消息队列！交换机为：{}，路由key为：{}，回复码为：{}", exchange, routingKey, replyCode);
        });
    }

}
