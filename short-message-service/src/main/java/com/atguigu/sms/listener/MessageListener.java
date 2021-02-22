package com.atguigu.sms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.sms.service.SmsService;
import com.rabbitmq.client.Channel;
import dto.ShortMessageDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@Component
public class MessageListener {

    @Autowired
    SmsService smsService;

    @RabbitListener(queues = "SHORT_MESSAGE_QUEUE")
    public void receiveMessage(String shortMessageDtoJson, Channel channel, Message message) throws IOException {
        try {
            System.out.println("*******" + shortMessageDtoJson);
            ShortMessageDto shortMessageDto = JSON.parseObject(shortMessageDtoJson, ShortMessageDto.class);
            // 生成并发送验证码
            boolean success = smsService.validateCodeOperation(shortMessageDto);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            if (!message.getMessageProperties().getRedelivered()) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }

}
