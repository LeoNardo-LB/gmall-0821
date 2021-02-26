package com.atguigu.gmall.cart.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.dto.CartRealTimeCartDto;
import com.atguigu.gmall.cart.service.CartService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
@Component
public class PriceListener {

    @Autowired
    CartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "PRICE_QUEUE", durable = "true"),
            exchange = @Exchange(value = "ITEM_EXCHANGE", ignoreDeclarationExceptions = "true", durable = "true", type = ExchangeTypes.TOPIC),
            key = "item.price.#"
    ))
    public void updateRealTimePrice(String cartPriceDto, Channel channel, Message message) throws IOException {
        try {
            // System.out.println("监听到价格变化" + cartPriceDto.toString());
            CartRealTimeCartDto cartDto = JSON.parseObject(cartPriceDto, CartRealTimeCartDto.class);
            cartService.updateRealTimePrice(cartDto.getSkuId(), cartDto.getCurrentPrice());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            if (!message.getMessageProperties().getRedelivered()) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

}
