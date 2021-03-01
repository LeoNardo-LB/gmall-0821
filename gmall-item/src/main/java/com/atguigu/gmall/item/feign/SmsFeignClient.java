package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.controller.Api.SmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/20
 **/
@FeignClient("sms-service")
public interface SmsFeignClient extends SmsGmallApi {

}
