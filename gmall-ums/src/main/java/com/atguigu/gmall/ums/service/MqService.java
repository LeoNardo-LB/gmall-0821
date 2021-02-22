package com.atguigu.gmall.ums.service;

import dto.ShortMessageDto;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
public interface MqService {

    void sendValidateCode2Mq(ShortMessageDto shortMessageDto);

}
