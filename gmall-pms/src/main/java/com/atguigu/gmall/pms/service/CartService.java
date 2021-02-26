package com.atguigu.gmall.pms.service;

import java.math.BigDecimal;

/**
 * Author: Administrator
 * Create: 2021/2/25
 **/
public interface CartService {

    void updateRealTimePrice(Long id, BigDecimal price);

}
