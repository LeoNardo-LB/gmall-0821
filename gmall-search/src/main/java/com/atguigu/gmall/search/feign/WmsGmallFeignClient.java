package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.WmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/1/31
 **/
@FeignClient("wms-service")
public interface WmsGmallFeignClient extends WmsGmallApi {

}
