package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.wms.api.WmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface GmallWmsClient extends WmsGmallApi {
}