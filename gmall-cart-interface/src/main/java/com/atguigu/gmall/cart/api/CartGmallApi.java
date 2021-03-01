package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.dto.Cart;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/28
 **/
public interface CartGmallApi {

    @GetMapping("/cart/checked/{userId}")
    public ResponseVo<List<Cart>> queryCheckedCarts(@PathVariable String userId);

}
