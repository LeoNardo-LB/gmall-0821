package com.atguigu.gmall.pms.Vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/1/20
 **/
@Data
public class SpuAttrVo {

    private Long attrId;

    private String attrName;

    private String valueSelected;

    public void setValueSelected(List<String> selectedList) {
        this.valueSelected = StringUtils.join(selectedList, ',');
    }

}
