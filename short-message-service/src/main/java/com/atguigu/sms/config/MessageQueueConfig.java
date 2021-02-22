package com.atguigu.sms.config;

import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/

@Configuration
public class MessageQueueConfig {

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange("SMS_TOPIC_EXCHANGE").durable(true).build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable("SHORT_MESSAGE_QUEUE").build();
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("sms.#").noargs();
    }

}
