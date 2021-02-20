package com.atguigu.gmall.controller.Dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@Data
@Accessors(chain = true)
public class ItemSaleVo {

    private String type; // 积分 满减 打折

    private String desc; // 描述信息

}
