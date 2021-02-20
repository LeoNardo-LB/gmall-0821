package com.atguigu.gmall.pms.entity;

import lombok.Data;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@Data
public class ItemGroupVo {

    private Long groupId;

    private String groupName;

    private List<AttrValueVo> attrValues;

}
