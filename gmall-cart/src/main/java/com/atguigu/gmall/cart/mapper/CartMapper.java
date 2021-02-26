package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.cart.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    int insertBatch(@Param("cartList") List<Cart> cartList);

}
