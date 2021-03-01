package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.CartGmallApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cart-service")
public interface CartFeignClient extends CartGmallApi {
}