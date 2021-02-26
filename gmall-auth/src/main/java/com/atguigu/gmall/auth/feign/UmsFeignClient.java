package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.UmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@FeignClient("ums-service")
public interface UmsFeignClient extends UmsGmallApi {

}
