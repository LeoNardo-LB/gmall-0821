package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ItemGroupVo;
import com.atguigu.gmall.pms.entity.SaleAttrValueVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@Data
@Accessors(chain = true)
public class ItemVo {

    // 面包屑部分
    // 三级分类
    private List<CategoryEntity> categories;

    // 品牌
    private Long brandId;

    private String brandName;

    // spu
    private Long spuId;

    private String spuName;

    // sku
    private Long skuId;

    private String title;

    private String subTitle;

    private BigDecimal price;

    private Integer weight;

    private String defaultImage;

    private List<String> images;

    // 营销信息
    private List<ItemSaleVo> sales;

    // 是否有货(默认无货)
    private Boolean store = false;

    // sku所属spu下的所有sku的销售属性
    // [{attrId: 3, attrName: '颜色', attrValues: '白色','黑色','粉色'},
    // {attrId: 8, attrName: '内存', attrValues: '6G','8G','12G'},
    // {attrId: 9, attrName: '存储', attrValues: '128G','256G','512G'}]
    private List<SaleAttrValueVo> saleAttrs;

    // 当前sku的销售属性：{3:'白色',8:'8G',9:'128G'}
    private Map<Long, String> saleAttr;

    // sku列表：{'白色,8G,128G': 4, '白色,8G,256G': 5, '白色,8G,512G': 6, '白色,12G,128G': 7}
    // 销售属性组合与Skuid的映射关系(用于到门口跳转)
    private String skusJson;

    // spu的海报信息
    private List<String> spuImages;

    // 规格参数组及组下的规格参数(带值)
    private List<ItemGroupVo> groups;

}
