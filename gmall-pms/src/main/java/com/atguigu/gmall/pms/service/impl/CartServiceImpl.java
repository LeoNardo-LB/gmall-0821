package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.dto.CartRealTimeCartDto;
import com.atguigu.gmall.pms.service.CartService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void updateRealTimePrice(Long skuId, BigDecimal price) {
        CartRealTimeCartDto cartDto = new CartRealTimeCartDto();
        cartDto.setSkuId(skuId);
        cartDto.setCurrentPrice(price);
        String cartDtoJson = JSON.toJSONString(cartDto);
        rabbitTemplate.convertAndSend("ITEM_EXCHANGE", "item.price.update", cartDtoJson);
    }

}
