package com.atguigu.gmall.search.listener;

import com.atguigu.gmall.search.service.SearchService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
@Component
public class ItemListener {

    @Autowired
    SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ITEM_QUEUE", durable = "true"),
            exchange = @Exchange(value = "ITEM_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = "item.*"
    ))
    public void addItem(String msg, Channel channel, Message message) throws IOException {
        try {
            long spuId = Long.parseLong(msg);
            searchService.createIndex(spuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            if (!message.getMessageProperties().getRedelivered()) {
                // 未重入队
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } else {
                // 重入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

}
