package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.controller.Api.GmallSmsApi;
import com.atguigu.gmall.pms.feign.impl.GmallSmsFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/1/21
 **/
@FeignClient(name = "sms-service" , fallback = GmallSmsFeignClientImpl.class)
public interface GmallSmsFeignClient extends GmallSmsApi {

}
