package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.PmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
@FeignClient("pms-service")
public interface PmsFeignClient extends PmsGmallApi {



}
