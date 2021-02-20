package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.PmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/1/31
 **/
@FeignClient("pms-service")
public interface PmsFeignClient extends PmsGmallApi {

}
