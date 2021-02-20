package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.PmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@FeignClient(value = "pms-service")
public interface PmsFeignClient extends PmsGmallApi {

}
