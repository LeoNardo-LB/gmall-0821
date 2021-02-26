package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.PmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface GmallPmsClient extends PmsGmallApi {
}