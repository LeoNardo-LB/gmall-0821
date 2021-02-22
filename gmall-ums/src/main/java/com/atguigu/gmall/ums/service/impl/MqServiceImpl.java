package com.atguigu.gmall.ums.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.ums.service.MqService;
import dto.ShortMessageDto;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@Service
public class MqServiceImpl implements MqService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void sendValidateCode2Mq(ShortMessageDto shortMessageDto) {
        rabbitTemplate.convertAndSend("SMS_TOPIC_EXCHANGE", "sms.validate", JSON.toJSONString(shortMessageDto));
    }

}
