package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.controller.Api.SmsGmallApi;
import com.atguigu.gmall.pms.feign.impl.SmsGmallFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/1/21
 **/
@FeignClient(name = "sms-service" , fallback = SmsGmallFeignClientImpl.class)
public interface SmsGmallFeignClient extends SmsGmallApi {

}
