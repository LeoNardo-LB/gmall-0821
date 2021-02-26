package com.atguigu.gmall.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
@Data
public class CartRealTimeCartDto {

    private Long skuId;

    private BigDecimal currentPrice;

}
