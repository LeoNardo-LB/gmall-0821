package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/20
 **/
@FeignClient("wms-service")
public interface WmsFeignClient extends GmallWmsApi {

}
