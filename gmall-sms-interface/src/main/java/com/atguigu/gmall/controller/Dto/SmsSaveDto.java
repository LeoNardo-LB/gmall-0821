package com.atguigu.gmall.controller.Dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/1/21
 **/
@Data
@Accessors(chain = true)
public class SmsSaveDto {

    private Long id;

    private Long skuId;

    // 成长积分
    private BigDecimal growBounds;

    // 购买积分
    private BigDecimal buyBounds;

    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;
     * 0 - 无优惠，成长积分是否赠送;
     * 1 - 无优惠，购物积分是否赠送;
     * 2 - 有优惠，成长积分是否赠送;
     * 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    // 满减活动
    private BigDecimal fullPrice;

    // 满减多少
    private BigDecimal reducePrice;

    // 是否叠加
    private Integer fullAddOther;

    // 满几件才能打折
    private Integer fullCount;

    // 折扣
    private BigDecimal discount;

    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer ladderAddOther;

}
