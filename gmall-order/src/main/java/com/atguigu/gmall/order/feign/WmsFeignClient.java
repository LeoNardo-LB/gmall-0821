package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.api.WmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface WmsFeignClient extends WmsGmallApi {

}