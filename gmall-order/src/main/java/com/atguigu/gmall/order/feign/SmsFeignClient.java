package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.controller.Api.SmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SmsFeignClient extends SmsGmallApi {
}