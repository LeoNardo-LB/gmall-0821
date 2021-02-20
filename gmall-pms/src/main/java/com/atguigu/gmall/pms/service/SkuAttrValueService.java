package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SaleAttrValueVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:22
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<SaleAttrValueVo> querySkuValueBySpuId(Long spuId);

    Map<Long, String> queryCurrentSkuAttrValue(Long skuId);

    String querySkuAttrMapping(Long spuId);

}

