package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {

    private Long skuId;

    private String title;

    private String defaultImage;

    private BigDecimal price;

    private Integer count;

    private BigDecimal weight;

    private List<SkuAttrValueEntity> saleAttrs; // 销售属性

    private List<ItemSaleVo> sales; // 营销信息

    private Boolean store = false; // 库存信息

}