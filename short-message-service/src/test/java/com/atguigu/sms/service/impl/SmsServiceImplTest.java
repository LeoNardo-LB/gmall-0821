package com.atguigu.sms.service.impl;

import com.atguigu.sms.service.SmsService;
import dto.ShortMessageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@SpringBootTest
class SmsServiceImplTest {

    @Autowired
    SmsService smsService;

    @Test
    void validateCodeOperation() {
        smsService.validateCodeOperation(new ShortMessageDto(1, "13335894150"));
    }

}