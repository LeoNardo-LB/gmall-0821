package com.atguigu.sms.service;

import dto.ShortMessageDto;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
public interface SmsService {

    boolean validateCodeOperation(ShortMessageDto shortMessageDto);

}
