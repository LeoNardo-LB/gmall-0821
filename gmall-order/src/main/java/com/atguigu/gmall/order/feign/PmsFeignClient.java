package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.pms.api.PmsGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface PmsFeignClient extends PmsGmallApi {
}