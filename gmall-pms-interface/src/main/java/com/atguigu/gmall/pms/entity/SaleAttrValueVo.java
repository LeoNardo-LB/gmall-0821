package com.atguigu.gmall.pms.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@Data
@Accessors(chain = true)
public class SaleAttrValueVo {

    private Long attrId;

    private String attrName;

    private Set<String> attrValues;

}