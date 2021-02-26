package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.wms.api.WmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/20
 **/
@FeignClient("wms-service")
public interface WmsGmallFeignClient extends WmsGmallApi {

}
