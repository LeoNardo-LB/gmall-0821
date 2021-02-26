package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.controller.Api.SmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface GmallSmsClient extends SmsGmallApi {
}