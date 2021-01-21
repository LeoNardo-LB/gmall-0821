package com.atguigu.gmall.pms.entity.Vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/1/20
 **/
@Data
@Accessors
public class SpuSaveVo extends SpuEntity {

    private List<String> spuImages;

    private List<SpuAttrVo> baseAttrs;

    private List<SkuSaveVo> skus;

}
